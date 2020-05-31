package group.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author: bbpatience
 * @date: 2019/3/30
 * @description: GroupReqParam
 **/
@Data
@AllArgsConstructor
public class GroupReqParam {
    private String name;
    private String portrait;
    private List<String> members;
}
