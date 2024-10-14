package art.yang.alarm.message;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author arTGOD
 * @Date 2024/10/14 9:42
 * @Description
 */
public class TextMessage implements Message {
    private String text;
    private List<String> mentionedMobileList;
    private List<String> mentionedList;
    private boolean isAtAll;


    public TextMessage(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isAtAll() {
        return isAtAll;
    }

    public void setIsAtAll(boolean isAtAll) {
        this.isAtAll = isAtAll;
    }

    public List<String> getMentionedMobileList() {
        return mentionedMobileList;
    }

    public void setMentionedMobileList(List<String> mentionedMobileList) {
        this.mentionedMobileList = mentionedMobileList;
    }

    public String toJsonString() {
        Map<String, Object> items = new HashMap<String, Object>();
        items.put("msgtype", "text");
        Map<String, Object> textContent = new HashMap<String, Object>();
        if (StringUtils.isBlank(text)) {
            throw new IllegalArgumentException("text should not be blank");
        }
        textContent.put("content", text);
        if(isAtAll) {
            if(mentionedMobileList==null) mentionedMobileList=new ArrayList<String>();
            mentionedMobileList.add("@all");
        }
        if(mentionedList!=null && !mentionedList.isEmpty()) {
            textContent.put("mentioned_list", mentionedList);
        }
        if (mentionedMobileList != null && !mentionedMobileList.isEmpty()) {
            textContent.put("mentioned_mobile_list", mentionedMobileList);
        }
        items.put("text", textContent);
        return JSONUtil.toJsonStr(items);
    }
}
