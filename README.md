
<h1>Name:Gal Drimer  ID:315871491</h1>
<p> Email: galdr@edu.jmc.ac.il</p>

video URL:https://www.youtube.com/watch?v=xtaCv8oZ3z4

<h1>video file was also in the folder under the name video but was deleted because the size was 171 mgb</h1>

the progream that i made is a site that also works as 
a chatlog of prudacts and of stores 

the site was made without using react as was the task
user info is saved with MySQL Database and the password that i decided to use is 1111111

admin username for the site is:admin & password is:admin123
 

<h1>Project Overview</h1>
<p>
A comprehensive chain store management website built with Spring Boot MVC + Thymeleaf for server-side rendering.
The system allows customers to browse products and stores, write reviews, and play a loyalty game to earn points, while administrators can manage the entire store network.

</p>

<h1>the site explained in short</h1>

<p>
when users enter the site the can choose to be on the home screen not logged to go to the login page
or to register to the site but this all the can do 

if user choose to register to the site it will remember the user with MySQL even after the user logsout and the progream IDEA that runs the site is also close the new user will not be forgetten
any user that was created using register will always be a customer never will he be a admin 

the the site for customer and admin is different:

for customer:
he can view is profile 
he can view all of the diffrent stores on the site 
he can view all of the product in the site 
he can leave a comment/review on a store or a product page (but the need admin to aprove it before it will be posted)
he can play the loyalty games to get a new high score in the game (every time he plays it he get 20 points for customer points) he choose what mod to play (how many questions he need to asnwer)
he als get points for leaveing comment/review on a store or a product page 
he as a profile page where he can view info about his own user and how many customer points he as there also he can see the Community Statistics

for admin:
he can't play the loyalty game when he enters the page of the game and tries to choose a game mod the moment he clicks on Start Playing! he get the massage: Only customers can play the loyalty game!
he can't leave a comment/review on a store or a product page there is no option for him (but he can view all of the products and the reviews that where left for them)
he can view is profile 
while he can't leave a comment/review on a store or a product page he can and he need to review every comment/review that was left and he need to decided if to aprove it or reject it before customers can see it
he can the add products to the products chatlog and store to the store product (if he adds products he can also add to them images)
he can add,delete or edit products and stores pages 
he can add,delete or edit questions in the  loyalty game
he can delete users from the site
</p>

<h1>what was used</h1>

<p>
Backend: Spring Boot 3.x, Spring MVC, Spring Security
Frontend: Thymeleaf, Bootstrap 5.1.3, HTML5, CSS3, JavaScript
Database: MySQL with JPA/Hibernate
Authentication: Spring Security with BCrypt password encoding
Architecture: MVC Pattern with Repository-Service-Controller layers
</p>


<h1>Features explained</h1>
<p>
 Store Management

Browse store locations across multiple cities and states
Detailed store information (hours, contact, features)
Store search and filtering by location, amenities
Customer reviews and ratings for stores

Product Catalog

Comprehensive product browsing with categories and brands
Advanced filtering (price range, stock status, search)
Product image upload and management
Customer product reviews and ratings

Loyalty Game System

Interactive knowledge quiz about products and stores
Multiple difficulty levels and question types
Customer points system and leaderboards
Game session management with progress tracking

<User Management

Customer Features:

User registration and profile management
Review and rating system
Loyalty points accumulation
Game history and statistics


Admin Features:

Complete CRUD operations for products, stores, and users
Review moderation system
Game question management
User account management



Security Features

Spring Security authentication and authorization
Role-based access control (Customer/Admin)
Password encryption with BCrypt
Session management with automatic game cleanup on logout

</p>


