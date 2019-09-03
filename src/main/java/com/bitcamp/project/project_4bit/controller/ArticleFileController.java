package com.bitcamp.project.project_4bit.controller;


import com.bitcamp.project.project_4bit.entity.Article;
import com.bitcamp.project.project_4bit.entity.ArticleFile;
import com.bitcamp.project.project_4bit.entity.File;
import com.bitcamp.project.project_4bit.service.ArticleFileService;
import com.bitcamp.project.project_4bit.service.ArticleService;
import com.bitcamp.project.project_4bit.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/articlefile")
public class ArticleFileController {

    @Autowired
    private ArticleFileService articleFileService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private FileService fileService;


    @PreAuthorize("hasAnyAuthority('NOTICE_WRITE','JOB_WRITE','PRO_WRITE','CBOARD_WRITE','CNOTICE_WRITE','LIBRARY_WRITE')")
    @RequestMapping(
            path = "/write",
            method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ArticleFile createArticleFile(
            @RequestParam(name = "articleId") Long articleId,
            @RequestParam(name = "fileId") Long fileId){

        ArticleFile newArticleFile = new ArticleFile();

        Article article = articleService.selectArticleId(articleId);
        File file = fileService.selectFileId(fileId);

        newArticleFile.setArticle(article);
        newArticleFile.setFile(file);

        return articleFileService.createArticleFile(newArticleFile);
    }

    // 첨부한 파일들을 읽는 것
    @PreAuthorize("hasAnyAuthority('NOTICE_READ','JOB_READ','PRO_READ','CBOARD_READ','CNOTICE_READ','LIBRARY_READ')")
    @RequestMapping(
            path = "/filelist",
            method = RequestMethod.GET,
            produces = {
                    MediaType.APPLICATION_JSON_UTF8_VALUE,
                    MediaType.APPLICATION_XML_VALUE})
    public List<ArticleFile> listOfFile(
            @RequestParam(name = "articleId", required = true) Long articleId){
        return articleFileService.findArticleFile(articleId);
    }
}
