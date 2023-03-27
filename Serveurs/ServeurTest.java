// package Serveurs;

// import Database.MessageManager;
// import Database.UserManager;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.Mock;
// import org.mockito.Mockito;
// import java.io.BufferedReader;
// import java.io.BufferedWriter;
// import java.io.IOException;
// import java.net.Socket;
// import java.sql.Connection;
// import java.util.Arrays;
// import java.util.HashSet;
// import java.util.Set;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.*;

// public class ServeurTest extends Mockito{

//     private Serveur serveur;

//     @Mock
//     private Socket socket;
//     @Mock
//     private BufferedReader input;
//     @Mock
//     private BufferedWriter output;
//     @Mock
//     private Connection conn;
//     @Mock
//     private UserManager userManager;
//     @Mock
//     private MessageManager messageManager;

//     @BeforeEach
//     public void setup() {
//         serveur = new Serveur(socket);
//         serveur.setConnection(conn);
//         serveur.setInput(input);
//         serveur.setOutput(output);
//         serveur.setUserManager(userManager);
//         serveur.setMessageManager(messageManager);
//     }

//     @Test
//     public void testExtractTagsFromMessage_noTags() {
//         String message = "This is a test message.";
//         Set<String> expectedTags = new HashSet<>();
//         Set<String> actualTags = serveur.extractTagsFromMessage(message);
//         assertEquals(expectedTags, actualTags);
//     }

//     @Test
//     public void testExtractTagsFromMessage_validTags() {
//         String message = "This is a test message. #tag1 #tag2";
//         Set<String> expectedTags = new HashSet<>(Arrays.asList("tag1", "tag2"));
//         Set<String> actualTags = serveur.extractTagsFromMessage(message);
//         assertEquals(expectedTags, actualTags);
//     }

//     @Test
//     public void testExtractTagsFromMessage_invalidTags() {
//         String message = "This is a test message. #ta#g1 #ta g2";
//         Set<String> expectedTags = new HashSet<>();
//         Set<String> actualTags = serveur.extractTagsFromMessage(message);
//         assertEquals(expectedTags, actualTags);
//     }

//     @Test
//     public void testGetAuthorFromBody_validAuthor() {
//         String body = "author:testUser\r\nThis is a test message.";
//         String expectedAuthor = "testUser";
//         String actualAuthor = serveur.getAuthorFromBody(body);
//         assertEquals(expectedAuthor, actualAuthor);
//     }

//     @Test
//     public void testGetAuthorFromBody_missingAuthor() {
//         String body = "This is a test message.";
//         String expectedAuthor = "";
//         String actualAuthor = serveur.getAuthorFromBody(body);
//         assertEquals(expectedAuthor, actualAuthor);
//     }

//     // Similar tests for getMessageFromBody(), addMessageToDatabase(),
//     // handleRcvIdsCommand(), handleRcvMsgCommand(), and extractValueFromBody()
//     // methods.

// }
