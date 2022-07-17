package hygge.blog.domain.enums;

/**
 * [PO_STATUS]文章类别节点类型
 *
 * @author Xavier
 * @date 2022/7/17
 */
public enum ArticleCategoryTreeTypeEnum {
    /**
     * 默认
     */
    DEFAULT(0, "DEFAULT"),
    /**
     * 路径节点(不允许挂载文章)
     */
    PATH(1, "PATH");

    ArticleCategoryTreeTypeEnum(Integer index, String value) {
        this.index = index;
        this.value = value;
    }

    public static ArticleCategoryTreeTypeEnum parse(Integer index) {
        if (index == null) {
            throw new IllegalArgumentException("Unexpected index of ArticleCategoryTreeTypeEnum,it can't be null.");
        }
        switch (index) {
            case 0:
                return ArticleCategoryTreeTypeEnum.DEFAULT;
            case 1:
                return ArticleCategoryTreeTypeEnum.PATH;
            default:
                throw new IllegalArgumentException("Unexpected index of ArticleCategoryTreeTypeEnum,it can't be " + index + ".");
        }
    }

    public static ArticleCategoryTreeTypeEnum parse(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Unexpected value of ArticleCategoryTreeTypeEnum,it can't be null.");
        }
        switch (value) {
            case "DEFAULT":
                return ArticleCategoryTreeTypeEnum.DEFAULT;
            case "PATH":
                return ArticleCategoryTreeTypeEnum.PATH;
            default:
                throw new IllegalArgumentException("Unexpected value of ArticleCategoryTreeTypeEnum,it can't be " + value + ".");
        }
    }

    /**
     * 序号
     */
    private Integer index;
    /**
     * 枚举值
     */
    private String value;

    /*非自动生成内容开始*/
    /*非自动生成内容结束*/

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}