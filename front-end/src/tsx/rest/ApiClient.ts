import axios from "axios";
import {message} from "antd";
import {PropertiesHelper, UrlHelper} from "../utils/UtilContainer";

axios.defaults.baseURL = "http://localhost:8080/blog-service/api";

axios.interceptors.response.use(function (response) {
    if (response.data == null) {
        return null;
    }
    let code = response.data?.code;

    // 对响应数据做点什么
    if (code == 200) {
        return response;
    } else if (code == 400 || code < 500000) {
        message.warn(response.data.msg, 5);
        return null;
    } else {
        message.error(response.data.msg, 5);
        return null;
    }
}, function (error) {
    // 对响应错误做点什么
    message.error(error.message, 5);
    return Promise.reject(error);
});

export interface HyggeResponse<T> {
    code: number;
    msg?: string;
    main?: T;
}

const emptyResponse = {} as HyggeResponse<any>;

export interface UserDto {
    uid: string;
    userAvatar: string;
    userSex: string;
    biography?: string;
    birthday?: number;
    phone?: string;
    email?: string;
}

export interface SignInResponse {
    user?: UserDto;
    token: string;
    refreshKey: string;
    deadline: number;
}

export class UserService {
    static getCurrentUser(): UserDto | null | undefined {
        let currentUserStringValue = localStorage.getItem('currentUser');
        if (PropertiesHelper.isStringNotNull()) {
            return null;
        }
        return JSON.parse(currentUserStringValue!) as UserDto;
    }

    static removeCurrentUser() {
        localStorage.removeItem('uid');
        localStorage.removeItem('token');
        localStorage.removeItem('refreshKey');
        localStorage.removeItem('currentUser');
    }

    static getHeader(currentHeader?: any): any {
        let result;

        if (currentHeader == null) {
            result = {};
        } else {
            result = currentHeader;
        }
        result.scope = "WEB";

        let currentSecretKey = UrlHelper.getQueryString("secretKey");
        if (currentSecretKey != null) {
            result.secretKey = currentSecretKey;
        }

        let currentUId = localStorage.getItem("uid");
        let currentToken = localStorage.getItem("token");
        let currentRefreshKey = localStorage.getItem("refreshKey");

        if (currentUId == null || currentToken == null || currentRefreshKey == null) {
            this.removeCurrentUser();
        } else {
            result.uid = currentUId;
            result.token = currentToken;
        }
        return result;
    }

    static signIn(ac: string, pw: string,
                  successHook?: (input?: HyggeResponse<SignInResponse>) => void,
                  beforeHook?: () => void,
                  finallyHook?: () => void): void {
        if (beforeHook != null) {
            beforeHook();
        }

        let requestData = null;
        if (PropertiesHelper.isStringNotNull(ac) && PropertiesHelper.isStringNotNull(pw)) {
            requestData = {
                "password": pw,
                "userName": ac
            };
        }

        axios.post("/sign/in", requestData).then((response) => {
                if (successHook != null && response != null) {
                    let data: HyggeResponse<SignInResponse> = response.data;
                    successHook(data);

                    if (data.main?.user != null) {
                        let user = data.main.user;
                        localStorage.setItem('uid', user.uid);
                        localStorage.setItem('token', data.main.token);
                        localStorage.setItem('refreshKey', data.main.token);
                        localStorage.setItem('currentUser', JSON.stringify(user));
                    }
                }
            }
        ).finally(() => {
            if (finallyHook != null) {
                finallyHook();
            }
        });
    }
}


export interface ArticleConfiguration {
    backgroundMusicType: string,
    mediaPlayType: string,
    src: string,
    coverSrc?: string,
    name?: string,
    artist?: string,
    lrc?: string
}

export interface TopicDto {
    tid: string,
    topicName: string,
    orderVal: number
}

export interface CategoryDto {
    cid: string,
    categoryName: string,
    orderVal: number
}

export interface CategoryTreeInfo {
    topicInfo: TopicDto,
    categoryList: CategoryDto[]
}

export interface ArticleDto {
    aid: string,
    configuration: ArticleConfiguration,
    categoryTreeInfo: CategoryTreeInfo,
    cid: string,
    title: string,
    imageSrc: string,
    summary: string,
    content: string,
    wordCount: number,
    pageViews: number,
    selfPageViews: number,
    orderGlobal: number,
    orderCategory: number,
    articleState: string
}

export class ArticleService {

    static findArticleByAid(aid?: string | null,
                            successHook?: (input?: HyggeResponse<ArticleDto>) => void,
                            beforeHook?: () => void,
                            finallyHook?: () => void): void {
        if (beforeHook != null) {
            beforeHook();
        }

        if (!PropertiesHelper.isStringNotNull(aid)) {
            if (successHook != null) {
                successHook();
            }
            return;
        }

        axios.get("/main/article/" + aid, {
            headers: UserService.getHeader()
        }).then((response) => {
                if (successHook != null && response != null) {
                    let data: HyggeResponse<ArticleDto> = response.data;
                    successHook(data);
                }
            }
        ).finally(() => {
            if (finallyHook != null) {
                finallyHook();
            }
        });
    }
}
