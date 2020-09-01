package com.lsc.blog.service;

import com.lsc.blog.NotFoundException;
import com.lsc.blog.dao.TypeReposity;
import com.lsc.blog.po.Type;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class TypeServiceImpl implements TypeService{

    @Autowired
    private TypeReposity typeReposity;

    @Transactional
    @Override
    public Type saveType(Type type) {
        return typeReposity.save(type);
    }

    @Transactional
    @Override
    public Type getType(Long id) {
       return typeReposity.getOne(id);
    }

    @Override
    public Type getTypeByName(String name) {
        return typeReposity.findByName(name);
    }

    @Transactional
    @Override
    public Page<Type> listType(Pageable pageable) {
        return typeReposity.findAll(pageable);
    }
    @Transactional
    @Override
    public Type updateType(Long id, Type type) {
        Type type1=typeReposity.getOne(id);
        if (type1 == null){
            throw new NotFoundException("不存在该类型");

        }
        BeanUtils.copyProperties(type,type1);

        return typeReposity.save(type1);
    }

    @Override
    public List<Type> listType() {
        return typeReposity.findAll();
    }

    @Override
    public List<Type> listTypeTop(Integer size) {
        Sort sort=Sort.by(Sort.Direction.DESC,"blogs.size");
        Pageable pageable=PageRequest.of(0,size,sort);
        return typeReposity.findTop(pageable);

    }

    @Override
    public void deleteType(Long id) {
        typeReposity.deleteById(id);
    }
}
