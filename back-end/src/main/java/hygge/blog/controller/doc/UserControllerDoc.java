package hygge.blog.controller.doc;

import hygge.blog.controller.base.HyggeBlogController;
import hygge.blog.domain.bo.HyggeBlogControllerResponse;
import hygge.blog.domain.dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * @author Xavier
 * @date 2022/7/17
 */
@Tag(name = "用户操作 Controller", description = "用户的创建修改查询操作")
public interface UserControllerDoc extends HyggeBlogController<ResponseEntity<?>> {
    @Operation(summary = "注册新用户", description = "注册新的默认权限用户")
    ResponseEntity<HyggeBlogControllerResponse<UserDTO>> createUser(UserDTO userDTO);

    @Operation(summary = "查询用户", description = "查询基本的用户信息")
    ResponseEntity<HyggeBlogControllerResponse<UserDTO>> findUser(String uid);

    @Operation(summary = "修改用户", description = "修改当前用户信息")
    @RequestBody(
            content = {@Content(schema =
            @Schema(ref = "#/components/schemas/UserDTO")
            )}
    )
    ResponseEntity<HyggeBlogControllerResponse<UserDTO>> updateUser(String uid, Map<String, Object> data);
}
