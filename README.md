#CHAT APP

A real time chat application made with Spring boot , MySQL DB and Web socket, and JWT authentication which allows : 

⦁	Secure user authentication implemented with JWT (JSON Web Tokens), ensuring stateless, tamper-proof, and scalable login sessions for all users
⦁	Public chatbox for testing
⦁	Private chatboxes for registered users
⦁	Chat history persistence
⦁	Real time message status update (sent->delivered->seen)
⦁	Pagination of messages with loose/infinite scrolling
⦁	An interactive frontend UI

Future Enhancements : 

Frontend/UI Improvments ->
⦁	Blue ticks and timestamps for messages
⦁	Unread message bubbles with counts
⦁	Real-time last_seen / online status
⦁	Lazy loading / dynamic visibility of chats

Backend/Scalability ->

⦁	Decoupling System
⦁	Chat-to-chat message tables
⦁	Support for multiple users with load balancing
 
