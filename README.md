# Instructions
### connect from a VM or another device
By using 
```java
ServerSocket serverSocket = new ServerSocket(PORT,50,InetAddress.getByName("0.0.0.0"));
```
I can connect from other clients at the ip obtained from running ```ifconfig``` in the terminal and 
using the corresponding ip of en0 to connect using: ```telenet <IP ADDRESS> 8888```. If you are using a 
VM to observe the realistic effects, as network adapter, you shouldn't use a NAS but a Bridged 
connection.

# TODO Objectives:
## 🌟 Phase 0: Code Audit & Setup (1-2 days)
**Goal**: Fully understand the existing codebase and prepare tools.

### Resources:
- [Java Sockets Tutorial (Oracle)](https://docs.oracle.com/javase/tutorial/networking/sockets/)
- [Java Concurrency Guide (Baeldung)](https://www.baeldung.com/java-concurrency)

### Steps:
1. [ ] Create sequence diagrams for client-server interactions
2. [ ] Document current authentication flow and message broadcasting logic
3. [ ] Set up debugging tools:
    - Wireshark for network analysis
    - IDE breakpoints for thread inspection
    - Test clients (Telnet/Netcat)

### Deliverables:
- Architecture diagram of current implementation
- List of identified security vulnerabilities

---

## 🔐 Phase 1: Security Foundation (3-5 days)
**Goal**: Implement secure authentication and encryption.

### Resources:
- [OWASP Authentication Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Authentication_Cheat_Sheet.html)
- [Bcrypt Java Implementation Guide](https://www.appsdeveloperblog.com/encrypt-user-password-example-java/)

### Steps:
1. [ ] Replace plaintext passwords with bcrypt hashing
2. [ ] Implement SSL/TLS using `SSLServerSocket`
3. [ ] Add session tokens with UUID
4. [ ] Introduce session expiration (15-minute timeout)

### Deliverables:
- Password storage using bcrypt
- Encrypted communication via SSL
- Session management system

---

## 🚀 Phase 2: Advanced Features (1-2 weeks)
**Goal**: Add unique functionality to stand out.

### Resources:
- [Netty Framework Guide](https://netty.io/wiki/user-guide.html)
- [SQLite Java Tutorial](https://www.sqlitetutorial.net/sqlite-java/)

### Steps:
1. [ ] Implement chat rooms:
    - `/join #general`
    - `/create #private`
2. [ ] Add message persistence using SQLite
3. [ ] Develop admin commands:
    - `/ban user`
    - `/purge #channel`
4. [ ] Introduce message formatting (Markdown/emoji)

### Deliverables:
- Multi-channel chat system
- Persistent message history
- Admin control panel

---

## 🧹 Phase 3: Code Quality (3-5 days)
**Goal**: Improve maintainability and documentation.

### Resources:
- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)

### Steps:
1. [ ] Refactor into packages:
    - `network`
    - `security`
    - `models`
2. [ ] Add logging with SLF4J + Logback
3. [ ] Write unit tests (70%+ coverage)
4. [ ] Generate Javadocs for all public methods

### Deliverables:
- Modularized codebase
- Test suite with CI pipeline
- API documentation

---

## ☁️ Phase 4: Deployment & Scaling (1 week)
**Goal**: Prepare for real-world usage.

### Resources:
- [Dockerizing Java Apps](https://spring.io/guides/gs/spring-boot-docker/)
- [AWS EC2 Documentation](https://docs.aws.amazon.com/ec2/index.html)

### Steps:
1. [ ] Dockerize the application
2. [ ] Configure load balancing
3. [ ] Implement connection limits (max 100 clients)
4. [ ] Set up monitoring:
    - Prometheus metrics
    - Grafana dashboard

### Deliverables:
- Docker image on Docker Hub
- Deployment guide for cloud platforms
- Performance benchmarks

---

## 🎨 Phase 5: Polish & Extras (Optional)
**Goal**: Add professional touches.

### Ideas:
- Custom GUI with JavaFX
- Message search functionality
- File sharing with virus scanning
- Chat bots integration
- Cross-server communication

### Recommended Resources:
- [JavaFX Official Docs](https://openjfx.io/)
- [Twitch Chat Bot Tutorial](https://dev.twitch.tv/docs/irc/)

---

# Learning Resources
## Books:
1. "Effective Java" by Joshua Bloch
2. "Secure Coding in Java" by Oracle Press
3. "Java Network Programming" by Elliotte Harold

## Courses:
- [Java Networking Deep Dive (Udemy)](https://www.udemy.com/course/java-socket-programming/)
- [Security Specialization (Coursera)](https://www.coursera.org/specializations/software-security)

## Tools:
- **VisualVM**: JVM monitoring
- **JMeter**: Load testing
- **Checkmarx**: Static code analysis (free tier available)

# Milestone Checklist
- [ ] Basic secure chat working
- [ ] Multi-room support implemented
- [ ] CI/CD pipeline operational
- [ ] Deployment to cloud platform
- [ ] Final presentation/documentation