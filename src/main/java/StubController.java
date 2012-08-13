import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class StubController {

    @RequestMapping({"/", "/index.html"})
    public String index() {
        return "index";
    }
}
