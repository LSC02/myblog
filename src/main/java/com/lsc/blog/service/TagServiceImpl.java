package com.lsc.blog.service;

import com.lsc.blog.NotFoundException;
import com.lsc.blog.dao.TagReposity;
import com.lsc.blog.po.Tag;
import com.lsc.blog.po.Type;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
@Service
public class TagServiceImpl implements TagService{

    @Autowired
    private TagReposity tagReposity;

    @Override
    public Tag saveTag(Tag tag) {
        return tagReposity.save(tag);
    }

    @Override
    public Tag getTag(Long id) {
        return tagReposity.getOne(id);
    }

    @Override
    public Tag getTagByName(String name) {
        return tagReposity.findByName(name);
    }

    @Override
    public Page<Tag> ListTag(Pageable pageable) {
        return tagReposity.findAll(pageable);
    }

    @Override
    public List<Tag> listTag() {
        return tagReposity.findAll();
    }

    @Override
    public List<Tag> listTag(String ids) {
        return tagReposity.findAllById(convertToList(ids));
    }

    @Override
    public List<Tag> listTagTop(Integer size) {
       Sort sort=Sort.by(Sort.Direction.DESC,"blogs.size");
        Pageable pageable= PageRequest.of(0,size,sort);
        return tagReposity.findTop(pageable);

    }


    private List<Long> convertToList(String ids){
        List<Long> list=new ArrayList<>();
        if (!"".equals(ids) && ids != null){
            String[] idarray=ids.split(",");
            for (int i=0; i< idarray.length; i++){
                list.add(new Long(idarray[i]));
            }
        }
        return list;
    }

    @Transactional
    @Override
    public Tag updateTag(Long id, Tag tag) {

        Tag tag1=tagReposity.getOne(id);
        if (tag1 == null){
            throw new NotFoundException("不存在该类型");

        }
        BeanUtils.copyProperties(tag,tag1);

        return tagReposity.save(tag1);
    }

    @Override
    public void deleteTag(Long id) {
        tagReposity.deleteById(id);

    }
}
