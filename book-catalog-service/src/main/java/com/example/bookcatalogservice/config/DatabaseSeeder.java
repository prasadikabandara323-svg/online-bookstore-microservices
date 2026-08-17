package com.example.bookcatalogservice.config;

import com.example.bookcatalogservice.model.Book;
import com.example.bookcatalogservice.repository.BookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseSeeder {

    @Bean
    CommandLineRunner initDatabase(BookRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                
                // 1. Chemistry Core Concepts
                repository.save(new Book(
                    null, "Chemistry Core Concepts", "Blackman, Southam, Lawrie", "1118742168", 4500.0,
                    "Education", 10, "bookcatelogimages/Chemistry.jpg",
                    "English", "Wiley", "9781118742167",
                    "Comprehensive guide covering core concepts of chemistry for students."
                ));

                // 2. Influence: The Psychology of Persuasion
                repository.save(new Book(
                    null, "Influence: The Psychology of Persuasion", "Robert Cialdini", "9780061241893", 3100.0,
                    "Psychology", 10, "bookcatelogimages/Influence.jpg",
                    "English", "Harper Business", "9789555738015",
                    "A classic examination of the psychology of compliance, explaining why people say 'yes' and how..."
                ));

                // 3. Emotional Intelligence
                repository.save(new Book(
                    null, "Emotional Intelligence", "Daniel Goleman", "055338371X", 2800.0,
                    "Psychology", 10, "bookcatelogimages/Emotional.jpg",
                    "English", "Bantam Books", "9780553383713",
                    "Why emotional intelligence can matter more than IQ for success."
                ));

                // 4. Clean Code
                repository.save(new Book(
                    null, "Clean Code", "Robert C. Martin", "0132350882", 5500.0,
                    "Education", 10, "bookcatelogimages/Clean.jpg",
                    "English", "Prentice Hall", "9780132350884",
                    "A handbook of agile software craftsmanship focused on writing clean code."
                ));

                // 5. Head First Design Patterns
                repository.save(new Book(
                    null, "Head First Design Patterns", "Eric Freeman & Elisabeth Robson", "0596007124", 6000.0,
                    "Education", 10, "bookcatelogimages/Design.jpg",
                    "English", "O'Reilly Media", "9780596007126",
                    "A brain-friendly guide to building extensible and maintainable object-oriented software."
                ));

                // 6. Atomic Habits
                repository.save(new Book(
                    null, "Atomic Habits", "James Clear", "0735211299", 3000.0,
                    "Psychology", 10, "bookcatelogimages/Automatic.jpg",
                    "English", "Avery", "9780735211299",
                    "An easy and proven way to build good habits and break bad ones."
                ));

                // 7. Gamperaliya
                repository.save(new Book(
                    null, "Gamperaliya", "Martin Wickramasinghe", "9555731245", 950.0,
                    "Novel", 10, "bookcatelogimages/Gamperaliya.jpg",
                    "Sinhala", "Sarasavi Publishers", "9789555731241",
                    "A timeless Sinhala masterpiece depicting the social changes in a traditional village."
                ));

                // 8. 1984
                repository.save(new Book(
                    null, "1984", "George Orwell", "0451524935", 1200.0,
                    "Novel", 10, "bookcatelogimages/1984.jpg",
                    "English", "Secker & Warburg", "9780451524935",
                    "A dystopian social science fiction novel and cautionary tale about totalitarianism."
                ));

                // 9. The Clever Little Rabbit
                repository.save(new Book(
                    null, "The Clever Little Rabbit", "Unknown", "1846102391", 850.0,
                    "Children", 10, "bookcatelogimages/Clever.jpg",
                    "English", "My Little Library", "9781846102394",
                    "A delightful collection of animal stories for young readers."
                ));

                // 10. Thinking, Fast and Slow
                repository.save(new Book(
                    null, "Thinking, Fast and Slow", "Daniel Kahneman", "9780374533557", 3200.0,
                    "Psychology", 10, "bookcatelogimages/Thinking.jpg",
                    "English", "Farrar, Straus and Giroux", "9780374533557",
                    "A groundbreaking tour of the mind and explains the two systems that drive the way we think."
                ));

                // 11. The Great Gatsby
                repository.save(new Book(
                    null, "The Great Gatsby", "F. Scott Fitzgerald", "9780743273565", 1400.0,
                    "Novel", 10, "bookcatelogimages/Great.jpg",
                    "English", "Charles Scribner's Sons", "9780743273565",
                    "A classic American novel depicting the Jazz Age and the tragic story of Jay Gatsby."
                ));

                // 12. To Kill a Mockingbird
                repository.save(new Book(
                    null, "To Kill a Mockingbird", "Harper Lee", "9780061120084", 1600.0,
                    "Novel", 10, "bookcatelogimages/Kill.jpg",
                    "English", "J. B. Lippincott & Co.", "9780061120084",
                    "A masterpiece of modern American literature focusing on racial injustice and moral growth."
                ));

                // 13. Madol Duwa
                repository.save(new Book(
                    null, "Madol Duwa", "Martin Wickramasinghe", "9555730451", 950.0,
                    "Novel", 10, "bookcatelogimages/Madol.jpg",
                    "Sinhala", "Sarasavi Publishers", "9789555730459",
                    "A popular children's adventure novel in Sri Lanka depicting the escapades of Upali and Jinadasa."
                ));

                // 14. The Magic Mango Tree
                repository.save(new Book(
                    null, "The Magic Mango Tree", "W. O. T. Fernando", "9559000123", 750.0,
                    "Children", 10, "bookcatelogimages/Magic.jpg",
                    "English", "Local Publishers", "9789559000124",
                    "An enchanting children's story filled with local folklore and magic adventures."
                ));

                // 15. Mahadenamuththa Saha Golayo
                repository.save(new Book(
                    null, "Mahadenamuththa Saha Golayo", "Kamal Tharaka", "9555739999", 850.0,
                    "Children", 10, "bookcatelogimages/Mahadenamuththa.jpg",
                    "Sinhala", "Sarasavi Publishers", "9789555739995",
                    "Humorous traditional Sri Lankan folklore stories about the foolish scholar Mahadenamuththa and his disciples."
                ));

                // 16. Man's Search for Meaning
                repository.save(new Book(
                    null, "Man's Search for Meaning", "Viktor E. Frankl", "9780807014271", 2400.0,
                    "Psychology", 10, "bookcatelogimages/Mans.jpg",
                    "English", "Beacon Press", "9780807014271",
                    "Psychiatrist Viktor Frankl's memoir detailing his experiences in Nazi concentration camps and his..."
                ));

                // 17. Design Patterns
                repository.save(new Book(
                    null, "Design Patterns", "Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides", "9780201633610", 6500.0,
                    "Education", 10, "bookcatelogimages/pattern.jpg",
                    "English", "Addison-Wesley", "9780201633610",
                    "A software engineering book describing recurring solutions to common problems in software design."
                ));

                // 18. The Pearl That Broke Its Shell
                repository.save(new Book(
                    null, "The Pearl That Broke Its Shell", "Nadia Hashimi", "9780062279705", 2700.0,
                    "Children", 10, "bookcatelogimages/Pearl.jpg",
                    "English", "William Morrow", "9780062279705",
                    "A powerful novel spanning generations of Afghan women fighting for freedom and self-determination."
                ));

                // 19. Viragaya
                repository.save(new Book(
                    null, "Viragaya", "Martin Wickramasinghe", "9555730214", 1100.0,
                    "Novel", 10, "bookcatelogimages/Viragaya.jpg",
                    "Sinhala", "Sarasavi Publishers", "9789555730213",
                    "A masterpiece of Sinhala literature exploring the complex inner psychology and detachment of Aravinda."
                ));

                // 20. Advanced Level Physics 2003 Essay Analysis
                repository.save(new Book(
                    null, "Advanced Level Physics 2003 Essay Analysis", "S. R. D. Rosa", "9558900111", 750.0,
                    "Education", 10, "bookcatelogimages/Physics.jpg",
                    "Sinhala", "GRC Books", "9789558900118",
                    "Detailed structured essay and structured question answers for G.C.E. Advanced Level Physics."
                ));

                // 21. The Power of Habit: Why We Do What We Do in Life and...
                repository.save(new Book(
                    null, "The Power of Habit: Why We Do What We Do in Life and...", "Charles Duhigg", "9781400069286", 2900.0,
                    "Psychology", 10, "bookcatelogimages/Power.jpg",
                    "English", "Random House", "9781400069286",
                    "An exploration of the science behind habit formation in our lives, companies, and societies."
                ));

                // 22. Punchi Yaluwo
                repository.save(new Book(
                    null, "Punchi Yaluwo", "W. B. C. Fernando", "9555734567", 650.0,
                    "Children", 10, "bookcatelogimages/Punchi.jpg",
                    "Sinhala", "Samayawardhana Publishers", "9789555734563",
                    "An engaging children's storybook featuring lovely animal characters and moral lessons."
                ));

                // 23. The Umbrella Thief (Kasadorobou)
                repository.save(new Book(
                    null, "The Umbrella Thief (Kasadorobou)", "Sybil Wettasinghe", "9784834001234", 900.0,
                    "Children", 10, "bookcatelogimages/Umbrella.jpg",
                    "Japanese / Multi", "Fukuinkan Shoten", "9784834001234",
                    "The internationally celebrated children's story about a colorful village umbrella and a thief."
                ));

                // 24. The Pragmatic Programmer: Your Journey to Mastery
                repository.save(new Book(
                    null, "The Pragmatic Programmer: Your Journey to Mastery", "David Thomas, Andrew Hunt", "9780135957059", 6200.0,
                    "Education", 10, "bookcatelogimages/Programming.jpg",
                    "English", "Addison-Wesley Professional", "9780135957059",
                    "A classic programming guide helping software developers to write better, more maintainable code."
                ));

                System.out.println("All books seeded to MongoDB successfully!");
            }
        };
    }
}