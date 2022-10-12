package fm.douban.app.control;

import fm.douban.model.Singer;
import fm.douban.service.SingerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author YangTao
 * @create 2022/4/1
 */
@Controller
public class SingerControl {
    private static final Logger LOG = LoggerFactory.getLogger(SingerControl.class);

    @Autowired
    private SingerService singerService;

    @GetMapping(path = "/user-guide")
    public String myMhz(Model model) {
        List<Singer> randomSingers =randomSingers();
        model.addAttribute("singers",randomSingers);
        return "userguide";
    }

    @GetMapping(path = "/singer/random")
    @ResponseBody
    public List<Singer> randomSingers(){
        List<Singer> randomSingers = new ArrayList<>();
        List<Singer> singers = singerService.getAll();
        if(singers!=null&&singers.size()>0) {
            for(int i =0;i<10;++i) {
                Random random = new Random();
                int n = random.nextInt(singers.size());
                randomSingers.add(singers.get(n));
            }

        }
        return randomSingers;
    }
}
