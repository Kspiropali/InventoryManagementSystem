package backend.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@SuppressWarnings({"deprecation", "ALL"})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "_messages")
public class Message {
    //5 static message types
    //Join, Leave for online offline users
    //Register, Remove for registered users(with isActivated=true) and deleted users
    //CHAT is for messages
    public enum MessageType {
        JOIN, LEAVE, CHAT, REGISTER, REMOVE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;


    public Message(MessageType messageType, String content){
        this.type = String.valueOf(messageType);
        this.content = content;
    }

    //large object data(because of images) or @Lob instead of length
    @Column(name = "content", nullable = true,length = 100000)
    private String content;
    @Column
    private String sender;
    @Column
    private String type;
    @Column
    private String destination;

    @CreationTimestamp
    @Column
    private Timestamp time;

    @Override
    public String toString(){
        return content+","+sender+","+type+","+destination+" "+time.getHours()+":"+time.getMinutes();
    }
}
