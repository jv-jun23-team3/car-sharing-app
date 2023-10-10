# <img src="icon.jpg" style="border-radius: 10px; width: 60px"> Car Sharing Service 

---

Welcome to the Car Sharing Service! This project is dedicated to revolutionizing urban mobility by providing a convenient and cost-effective solution for sharing vehicles within communities. Our platform is designed to enhance the efficiency of transportation while reducing costs for users. Whether you're a developer or someone seeking an innovative way to share rides, our Car Sharing Service is here to help. Join us on this journey towards a more connected future as we empower communities to take control of their transportation needs. Explore our project, contribute to its development, and let's drive change together!

## Table of content

---
- [Technologies and Tools](#technologies-and-tools)
- [Functionalities](#functionalities)
- [Getting Started](#getting-started)
- [Challenges Faced](#challenges-and-solutions)
- [Contributing](#contributions)

## Technologies and Tools

---

1. **Spring Boot:** Empowering rapid application development, Spring Boot enables us to build and iterate our platform swiftly.

2. **Spring Security:** With a focus on user authentication and data security, Spring Security ensures the safety of our users and their data.

3. **Spring Data JPA:** Simplifying database operations, Spring Data JPA streamlines data management, making it efficient and effective.

4. **Swagger:** Offering clear and accessible API documentation, Swagger helps developers understand and utilize our platform's functionalities seamlessly.

5. **Stripe:** Streamlining online payments, Stripe provides a secure and user-friendly payment processing solution for our platform.

6. **Telegram API:** Keeping users informed, the Telegram API facilitates instant notifications, enhancing the overall user experience.

7. **Docker:** Simplifying deployment, Docker's containerization technology ensures our application is easily portable and scalable across different environments.

8. **AWS:** Reliable hosting and scalability are made possible by AWS, ensuring our application runs smoothly and efficiently.

## Functionalities

---

- **AuthController:** Manages authentication and authorization processes, ensuring secure access 
  to the Car Sharing Service.

- **CarController:** Handles car-related functionalities, including listing available cars, 
  booking, and returning vehicles.

- **PaymentController:** Facilitates payment processing and manages transactions for car rentals.

- **RentalController:** Manages the rental process, including creating, updating, and canceling 
  rental reservations.

- **UserController:** Handles user-related operations, such as registration, profile management, 
  and authentication.

## Getting Started

---

### Docker

1. Build the Docker image using the provided Dockerfile:

   ```bash
   docker-compose build
   ```

2. Run the container with the built image:

   ```bash
   docker-compose up
   ```

3. Your API is now accessible at [http://localhost:8088](http://localhost:8088)
4. You can use Postman for sending requests. Here is our [postman collection](Car_Sharing_App.postman_collection.json).
### AWS

You also can use our API with swagger just in [one click](http://ec2-52-90-65-210.compute-1.amazonaws.com/api/swagger-ui/index.html#/)

## Challenges and Solutions

---

In the development of our Car Sharing Service project, we encountered several challenges, particularly in the integration of two critical components: the Telegram API and the Stripe API.

### Telegram API Integration

**Challenge:** Implementing the Telegram API posed a unique challenge as it was the first time our team had worked with this particular messaging platform. We needed to ensure that notifications were sent seamlessly to users through Telegram.

**Solution:** To overcome this challenge, we conducted thorough research, followed the Telegram API documentation meticulously, and iteratively tested our implementation. This allowed us to successfully integrate Telegram for instant notifications, enhancing user communication and experience within our platform.

### Stripe API Integration

**Challenge:** Integrating the Stripe API required careful handling of payment processing, user data, and security. This was a complex task, especially considering the sensitive nature of financial transactions.

**Solution:** To address this challenge, we followed industry best practices for payment processing, including tokenization and secure data transmission. Additionally, we regularly reviewed and updated our integration to maintain compliance with Stripe's security standards. As a result, we established a robust payment system that ensures seamless and secure online transactions for our users.

These challenges served as valuable learning experiences for our team, and the solutions implemented have not only enhanced the functionality of our Car Sharing Service but have also expanded our expertise in working with third-party APIs. We are committed to continually improving and refining these integrations to provide the best possible user experience.

## Contributions

---

We welcome contributions to the Car Sharing Service project from developers, enthusiasts, and anyone interested in making urban mobility more efficient and sustainable. Whether you want to fix a bug, enhance existing features, or propose new ones, your contributions are highly valuable.