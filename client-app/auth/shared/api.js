/**
 * All requests go through the API GATEWAY (port 8080), never directly to
 * the Auth Service (8081). The Gateway attaches the X-API-KEY header
 * automatically - the frontend never needs to know the API key.
 */
const API_BASE = "http://localhost:8080/api/auth";

function getToken() {
  return localStorage.getItem("token");
}

function setSession({ token, role, fullName, email, profileImage }) {
  if (token) localStorage.setItem("token", token);
  if (role) localStorage.setItem("role", role);
  if (fullName) localStorage.setItem("fullName", fullName);
  if (email) localStorage.setItem("email", email);
  if (profileImage !== undefined) {
    if (profileImage) localStorage.setItem("profileImage", profileImage);
    else localStorage.removeItem("profileImage");
  }
}

function clearSession() {
  localStorage.removeItem("token");
  localStorage.removeItem("role");
  localStorage.removeItem("fullName");
  localStorage.removeItem("email");
  localStorage.removeItem("profileImage");
}

function authHeaders() {
  const token = getToken();
  return token ? { Authorization: `Bearer ${token}` } : {};
}

async function request(path, { method = "GET", body, auth = false } = {}) {
  const headers = { "Content-Type": "application/json" };
  if (auth) Object.assign(headers, authHeaders());

  const response = await fetch(`${API_BASE}${path}`, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined,
  });

  let data = {};
  try {
    data = await response.json();
  } catch {
    // no JSON body - fine
  }

  if (!response.ok) {
    const message = data.message || `Request failed (${response.status})`;
    throw new Error(message);
  }

  return data;
}

const AuthAPI = {
  register: (fullName, email, password) =>
    request("/register", { method: "POST", body: { fullName, email, password } }),

  login: async (email, password) => {
    const data = await request("/login", { method: "POST", body: { email, password } });
    setSession({
      token: data.token,
      role: data.role,
      fullName: data.fullName,
      email: data.email,
      profileImage: data.profileImage,
    });
    return data;
  },

  forgotPassword: (email) =>
    request("/forgot-password", { method: "POST", body: { email } }),

  verifyOtp: (email, otp) =>
    request("/verify-otp", { method: "POST", body: { email, otp } }),

  resetPassword: (email, otp, newPassword) =>
    request("/reset-password", { method: "POST", body: { email, otp, newPassword } }),

  updateProfile: async (fullName, profileImage) => {
    const data = await request("/profile", {
      method: "PUT",
      auth: true,
      body: { fullName, profileImage },
    });
    setSession({ fullName: data.fullName, profileImage: data.profileImage });
    return data;
  },

  deleteAccount: () =>
    request("/profile", { method: "DELETE", auth: true }),

  logout: () => {
    clearSession();
  },

  isLoggedIn: () => Boolean(getToken()),
  getRole: () => localStorage.getItem("role"),
  getFullName: () => localStorage.getItem("fullName"),
  getEmail: () => localStorage.getItem("email"),
  getProfileImage: () => localStorage.getItem("profileImage"),
};
