package com.cyanrocks.ui.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cyanrocks.ui.dao.entity.UiEnum;
import com.cyanrocks.ui.dao.mapper.UiEnumMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author wjq
 * @Date 2024/9/19 16:49
 */
@Service
public class CommonSettingService {

    @Autowired
    private UiEnumMapper uiEnumMapper;

    public List<UiEnum> getEnumList(String type){
        return uiEnumMapper.selectList(Wrappers.<UiEnum>lambdaQuery().eq(UiEnum::getType,type));
    }

    public void setEnumList(List<UiEnum> reqs){
        reqs.forEach(req->{
            if (null == req.getId()){
                uiEnumMapper.insert(req);
            }else {
                uiEnumMapper.updateById(req);
            }
        });
    }

    public void deleteEnum(Long id){
        uiEnumMapper.deleteById(id);
    }
}
