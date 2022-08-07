package hygge.blog.controller;

import hygge.blog.controller.doc.HomePageControllerDoc;
import hygge.blog.domain.bo.HyggeBlogControllerResponse;
import hygge.blog.domain.dto.QuoteInfo;
import hygge.blog.domain.dto.inner.ArticleSummaryInfo;
import hygge.blog.domain.dto.inner.TopicOverviewInfo;
import hygge.blog.service.HomePageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Xavier
 * @date 2022/7/25
 */
@RestController
@RequestMapping(value = "/blog-service/api/main")
public class HomePageController implements HomePageControllerDoc {
    @Autowired
    private HomePageServiceImpl homePageService;

    @Override
    @GetMapping("/home/fetch")
    public ResponseEntity<HyggeBlogControllerResponse<TopicOverviewInfo>> homepageFetch() {
        return (ResponseEntity<HyggeBlogControllerResponse<TopicOverviewInfo>>) success(homePageService.fetch());
    }

    @Override
    @GetMapping("/home/fetch/topic/{tid}")
    public ResponseEntity<HyggeBlogControllerResponse<ArticleSummaryInfo>> topicInfoFetch(@PathVariable("tid") String tid,
                                                                                          @RequestParam(required = false, defaultValue = "1") int currentPage,
                                                                                          @RequestParam(required = false, defaultValue = "5") int pageSize) {
        return (ResponseEntity<HyggeBlogControllerResponse<ArticleSummaryInfo>>) success(homePageService.findArticleSummaryOfTopic(tid, currentPage, pageSize));
    }

    @Override
    @GetMapping("/home/fetch/category/{cid}")
    public ResponseEntity<HyggeBlogControllerResponse<ArticleSummaryInfo>> categoryInfoFetch(@PathVariable("cid") String cid,
                                                                                             @RequestParam(required = false, defaultValue = "1") int currentPage,
                                                                                             @RequestParam(required = false, defaultValue = "5") int pageSize) {
        return (ResponseEntity<HyggeBlogControllerResponse<ArticleSummaryInfo>>) success(homePageService.findArticleSummaryOfCategory(cid, currentPage, pageSize));
    }

    @Override
    @GetMapping("/home/fetch/quote")
    public ResponseEntity<HyggeBlogControllerResponse<QuoteInfo>> quoteInfoFetch(@RequestParam(required = false, defaultValue = "1") int currentPage,
                                                                                 @RequestParam(required = false, defaultValue = "5") int pageSize) {
        return (ResponseEntity<HyggeBlogControllerResponse<QuoteInfo>>) success(homePageService.findQuoteInfo(currentPage, pageSize));
    }


}
