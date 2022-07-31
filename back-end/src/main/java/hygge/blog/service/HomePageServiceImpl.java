package hygge.blog.service;

import hygge.blog.common.HyggeRequestContext;
import hygge.blog.common.HyggeRequestTracker;
import hygge.blog.domain.dto.CategoryDto;
import hygge.blog.domain.dto.HomepageFetchResult;
import hygge.blog.domain.dto.TopicDto;
import hygge.blog.domain.dto.inner.ArticleSummaryInfo;
import hygge.blog.domain.dto.inner.TopicOverviewInfo;
import hygge.blog.domain.mapper.PoDtoMapper;
import hygge.blog.domain.po.ArticleCountInfo;
import hygge.blog.domain.po.Category;
import hygge.blog.domain.po.Topic;
import hygge.blog.domain.po.User;
import hygge.web.template.HyggeWebUtilContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Xavier
 * @date 2022/7/25
 */
@Service
public class HomePageServiceImpl extends HyggeWebUtilContainer {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private TopicServiceImpl topicService;
    @Autowired
    private CategoryServiceImpl categoryService;
    @Autowired
    private ArticleServiceImpl articleService;

    public HomepageFetchResult fetch() {
        HyggeRequestContext context = HyggeRequestTracker.getContext();
        User currentUser = context.getCurrentLoginUser();

        List<Topic> topicList = topicService.findAllTopic();

        List<Category> categoryList = categoryService.getAccessibleCategoryList(currentUser, null);

        List<Integer> accessibleCategoryIdList = collectionHelper.filterNonemptyItemAsArrayList(false, categoryList, Category::getCategoryId);

        List<ArticleCountInfo> articleCountInfoList = articleService.findArticleCountInfo(accessibleCategoryIdList, context.isGuest() ? null : currentUser.getUserId());

        HomepageFetchResult result = HomepageFetchResult.builder().topicOverviewInfoList(new ArrayList<>()).build();
        for (Topic topic : topicList) {
            TopicDto topicDto = PoDtoMapper.INSTANCE.poToDto(topic);
            TopicOverviewInfo topicOverviewInfo = TopicOverviewInfo.builder().topicInfo(topicDto).categoryListInfo(new ArrayList<>()).build();

            for (Category category : categoryList) {
                if (!category.getTopicId().equals(topic.getTopicId())) {
                    continue;
                }
                CategoryDto categoryDto = PoDtoMapper.INSTANCE.poToDto(category);

                Optional<ArticleCountInfo> articleCountInfoTemp = articleCountInfoList.stream().filter(articleCountInfo -> articleCountInfo.getCategoryId().equals(category.getCategoryId())).findFirst();
                Integer count = articleCountInfoTemp.map(articleCountInfo -> articleCountInfo.getCount().intValue()).orElse(0);
                if (count > 0) {
                    categoryDto.setArticleCount(count);
                    topicOverviewInfo.getCategoryListInfo().add(categoryDto);
                }
            }
            if (!topicOverviewInfo.getCategoryListInfo().isEmpty()) {
                result.getTopicOverviewInfoList().add(topicOverviewInfo);
            }
        }

        if (result.getTopicOverviewInfoList().isEmpty()) {
            return null;
        }
        return result;
    }

    public ArticleSummaryInfo findArticleSummaryOfTopic(String tid, int currentPage, int pageSize) {
        HyggeRequestContext context = HyggeRequestTracker.getContext();
        User currentUser = context.getCurrentLoginUser();

        Topic topic = topicService.findTopicByTid(tid, false);
        List<Category> categoryList = categoryService.getAccessibleCategoryList(currentUser, collectionHelper.createCollection(topic.getTopicId()));

        List<Integer> accessibleCategoryIdList = collectionHelper.filterNonemptyItemAsArrayList(false, categoryList, Category::getCategoryId);
        return articleService.findArticleSummaryInfoByCategoryId(accessibleCategoryIdList, context.isGuest() ? null : currentUser.getUserId(), currentPage, pageSize);
    }

}
