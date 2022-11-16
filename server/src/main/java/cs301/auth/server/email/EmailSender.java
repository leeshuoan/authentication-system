package cs301.auth.server.email;

public interface EmailSender {
    void send(String to, String email);
}

