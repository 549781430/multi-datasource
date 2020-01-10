import com.jyblife.datasource.Application;
import com.jyblife.datasource.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
@Slf4j
public class TestApp {
    @Autowired
    private TestService testService;

    /**
     * 使用断言
     */
    @Test
    public void testSelect() {
        testService.update();
    }

    @Before
    public void testBefore() {
        log.info("单元测试开始");
    }

    @After
    public void testAfter() {
        log.info("单元测试完成");
    }
}
