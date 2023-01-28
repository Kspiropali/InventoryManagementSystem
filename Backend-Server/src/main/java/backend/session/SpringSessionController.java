package backend.session;


import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/session")
public class SpringSessionController {

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        List<String> messages = (List<String>) session.getAttribute("SESSION_MESSAGES");

        if (messages == null) {
            messages = new ArrayList<>();
        }
        model.addAttribute("sessionMessages", messages);
        model.addAttribute("sessionId", session.getId());

        return model.toString();
    }

    @PostMapping("/add")
    public String persistMessage(@RequestParam("msg") String msg, HttpServletRequest request) {

        List<String> msgs = (List<String>) request.getSession().getAttribute("SESSION_MESSAGES");
        if (msgs == null) {
            msgs = new ArrayList<>();
            request.getSession().setAttribute("SESSION_MESSAGES", msgs);
        }
        msgs.add(msg);
        request.getSession().setAttribute("SESSION_MESSAGES", msgs);
        return "Message Added";
    }

    @PostMapping("/destroy")
    public String destroySession(HttpServletRequest request) {
        request.getSession().invalidate();
        return "Session Destroyed";
    }
}