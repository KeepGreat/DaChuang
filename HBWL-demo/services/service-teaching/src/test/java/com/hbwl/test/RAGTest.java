package com.hbwl.test;

import com.hbwl.ai.AITeacherService;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.Filter;
import dev.langchain4j.store.embedding.filter.comparison.IsEqualTo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

@SpringBootTest
public class RAGTest {

    @Autowired
    private EmbeddingStore<TextSegment> embeddingStore;

    @Autowired
    private EmbeddingModel embeddingModel;

    @Autowired
    private AITeacherService aiTeacherService;

    @Test
    public void test01(){
        //1.从文件存储中加载文件
        //加载全部文件
//        Document documentAll = FileSystemDocumentLoader.loadDocument(pathStorageLocation, new ApacheTikaDocumentParser());
        //加载指定文件
        String fileName = "267d2653-58be-4b92-830c-ab4b81a155da.pdf"; //JAVA开发文档
        Document document = FileSystemDocumentLoader.loadDocument("../../uploads/files/" + fileName, new ApacheTikaDocumentParser());//注意单元测试的工作目录不一样
        //加入元数据便于后续管理
        document.metadata().put("file_id", "3"); //这里应当与数据库中file_content表中的id列对应
        System.out.println(document.metadata());

        //2.将文件从document分割为TextSegment
        DocumentByParagraphSplitter splitter = new DocumentByParagraphSplitter(500, 100);
        List<TextSegment> textSegments = splitter.split(document);
        System.out.println(textSegments.get(0).metadata());

        //3.将TextSegment转换为向量存储
        List<Embedding> embeddings = embeddingModel.embedAll(textSegments).content();
        embeddingStore.addAll(embeddings, textSegments);

        //上述操作只需执行一次
        //4.从数据库中查询相关向量
        Embedding query = embeddingModel.embed("错误码A0200是什么意思").content();

        EmbeddingSearchRequest request = new EmbeddingSearchRequest(query, 5, 0.75, null);
        List<EmbeddingMatch<TextSegment>> matches = embeddingStore.search(request).matches();
        for (EmbeddingMatch<TextSegment> match : matches) {
            System.out.println(match.embedded().text());
        }

        //5.从数据库中删除相关向量
        Filter filter = new IsEqualTo("file_id", 3);
        embeddingStore.removeAll(filter);
    }

    @Test
    public void test02(){
//        Flux<String> flux = aiTeacherService.answerQuestion("请问错误码A0100是什么意思");
//        StepVerifier.create(flux)
//                .thenConsumeWhile(element -> {
//                    System.out.print(element);
//                    return true;
//                })
//                .verifyComplete();
    }
}
