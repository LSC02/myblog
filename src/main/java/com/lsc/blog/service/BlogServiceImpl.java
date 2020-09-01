package com.lsc.blog.service;

import com.lsc.blog.NotFoundException;
import com.lsc.blog.dao.BlogReposity;
import com.lsc.blog.po.Blog;
import com.lsc.blog.po.Type;
import com.lsc.blog.util.MarkdownUtils;
import com.lsc.blog.util.MyBeanUtils;
import com.lsc.blog.vo.BlogQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.*;


@Service
public class BlogServiceImpl implements BlogService{

    @Autowired
    private BlogReposity blogReposity;


    @Override
    public Blog getBlog(Long id) {
        return blogReposity.getOne(id);
    }

    @Transactional
    @Override
    public Blog getAndConvert(Long id) {
        Blog blog=blogReposity.getOne(id);
        if (blog ==null){
            throw new NotFoundException("该博客不存在");
        }
        Blog b =new Blog();
        BeanUtils.copyProperties(blog,b);
        String content=b.getContent();

        b.setContent(MarkdownUtils.markdownToHtmlExtensions(content));

        blogReposity.updateViews(id);
        return b;
    }

    @Override
    public Page<Blog> listBlog(Pageable pageable, BlogQuery blog) {
        return blogReposity.findAll(new Specification<Blog>() {
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates=new ArrayList<>();
                if (!"".equals(blog.getTitle()) && blog.getTitle() != null){
                    predicates.add(criteriaBuilder.like(root.<String>get("title"),"%"+blog.getTitle()+"%"));
                }
                if (blog.getTypeId() != null){
                    predicates.add(criteriaBuilder.equal(root.<Type>get("type").get("id"),blog.getTypeId()));
                }
                if (blog.isRecommend()){
                    predicates.add(criteriaBuilder.equal(root.<Boolean>get("recommend"),blog.isRecommend()));
                }
                criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()]));
                return null;
            }
        },pageable);
    }

    @Override
    public Page<Blog> listBlog(Pageable pageable) {
        return blogReposity.findAll(pageable);
    }

    @Override
    public Page<Blog> listBlog(Long tagId, Pageable pageable) {
        return blogReposity.findAll(new Specification<Blog>() {
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> cq, CriteriaBuilder cd) {
                Join join=root.join("tags");
                return cd.equal(join.get("id"),tagId);
            }
        },pageable);
    }

    @Override
    public List<Blog> listRecommendBlogTop(Integer size) {
        Sort sort=Sort.by(Sort.Direction.DESC,"updateTime");
        Pageable pageable= PageRequest.of(0,size,sort);

        return blogReposity.findTop(pageable);
    }

    @Override
    public Page<Blog> listBlog(String query, Pageable pageable) {
        return blogReposity.findByQuery(query,pageable);
    }

    @Override
    public Map<String, List<Blog>> archiveBlog() {
        List<String> years=blogReposity.findGroupYear();
        Map<String,List<Blog>> map=new HashMap<>();
        for (String year : years){
            map.put(year,blogReposity.findByYear(year));
        }
        return map;
    }

    @Override
    public Long countBlog() {
        return blogReposity.count();
    }

    @Transactional
    @Override
    public Blog saveBlog(Blog blog) {
        if (blog.getId() == null){
            blog.setCreateTime(new Date());

            blog.setUpdateTime(new Date());
            blog.setViews(0);
        }else {
            blog.setUpdateTime(new Date());
        }

        return blogReposity.save(blog);
    }

    @Transactional
    @Override
    public Blog updateBlog(Long id, Blog blog) {
       Blog b=blogReposity.getOne(id);
        if (b==null){
            throw new NotFoundException("该博客不存在");
        }
        BeanUtils.copyProperties(blog,b, MyBeanUtils.getNullPropertyNames(blog));
        b.setUpdateTime(new Date());
        return blogReposity.save(b);
    }
    @Transactional
    @Override
    public void deleteBlog(Long id) {
        blogReposity.deleteById(id);

    }
}
