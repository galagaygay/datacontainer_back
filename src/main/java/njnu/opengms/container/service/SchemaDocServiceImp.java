package njnu.opengms.container.service;

import com.mongodb.BasicDBObject;
import njnu.opengms.container.dto.schemadoc.AddSchemaDocDTO;
import njnu.opengms.container.dto.schemadoc.FindSchemaDocDTO;
import njnu.opengms.container.dto.schemadoc.UpdateSchemaDocDTO;
import njnu.opengms.container.enums.ResultEnum;
import njnu.opengms.container.exception.MyException;
import njnu.opengms.container.pojo.SchemaDoc;
import njnu.opengms.container.repository.SchemaDocRepository;
import njnu.opengms.container.service.common.BaseService;
import njnu.opengms.container.vo.SchemaDocVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName SchemaDocService
 * @Description todo
 * @Author sun_liber
 * @Date 2018/12/17
 * @Version 1.0.0
 */
@Service
public class SchemaDocServiceImp implements BaseService<SchemaDoc, AddSchemaDocDTO, UpdateSchemaDocDTO, FindSchemaDocDTO, SchemaDocVO, String> {

    @Autowired
    SchemaDocRepository schemaDocRepository;

    @Autowired
    MongoTemplate mongoTemplate;


    @Override
    public void add(AddSchemaDocDTO addDTO) {
        if (schemaDocRepository.findByName(addDTO.getName()) != null) {
            throw new MyException(ResultEnum.EXIST_OBJECT);
        }
        SchemaDoc schemaDoc = new SchemaDoc();
        BeanUtils.copyProperties(addDTO, schemaDoc);
        schemaDoc.setCreateDate(new Date());
        schemaDocRepository.insert(schemaDoc);
    }

    @Override
    public void remove(String id) {
        schemaDocRepository.deleteById(id);
    }

    /**
     * @param findDTO
     *
     * @return
     */
    @Override
    public Page<SchemaDocVO> list(FindSchemaDocDTO findDTO) {
        SchemaDoc schemaDoc = new SchemaDoc();
        BeanUtils.copyProperties(findDTO, schemaDoc);

        // 只包含字符串的开始、结束、包含和正则匹配，以及其他字段的精确匹配
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("description", match -> match.contains().ignoreCase())
                .withMatcher("name", match -> match.contains().ignoreCase())
                .withIncludeNullValues();

        Example<SchemaDoc> schemaDocExample = Example.of(schemaDoc, matcher);

        PageRequest pageRequest;
        if (findDTO.getProperties() == null) {
            //不排序
            pageRequest = PageRequest.of(findDTO.getPage() - 1, findDTO.getPageSize());
        } else {
            //排序
            Sort sort = new Sort(findDTO.getAsc() ? Sort.Direction.ASC : Sort.Direction.DESC, findDTO.getProperties());
            pageRequest = PageRequest.of(findDTO.getPage() - 1, findDTO.getPageSize(), sort);
        }
        Page<SchemaDoc> page = schemaDocRepository.findAll(schemaDocExample, pageRequest);
        List<SchemaDocVO> listVO = new ArrayList<>();
        page.getContent().forEach(el -> {
            SchemaDocVO vo = new SchemaDocVO();
            BeanUtils.copyProperties(el, vo);
            listVO.add(vo);
        });
        Page<SchemaDocVO> pageVo = new PageImpl<>(listVO, page.getPageable(), page.getTotalElements());
        return pageVo;
    }

    @Override
    public SchemaDoc get(String s) {
        return schemaDocRepository.findById(s).orElseGet(() -> {
            System.out.println("有人乱查数据库！！");
            throw new MyException(ResultEnum.NO_OBJECT);
        });
    }

    @Override
    public SchemaDoc getByExample(SchemaDoc schemaDoc) {
        return schemaDocRepository.findOne(Example.of(schemaDoc)).orElseGet(() -> {
            System.out.println("有人乱查数据库！！");
            throw new MyException(ResultEnum.NO_OBJECT);
        });
    }

    @Override
    public long count() {
        return schemaDocRepository.count();
    }

    public List<SchemaDoc> getTop10() {
        BasicDBObject dbObject = new BasicDBObject();
        BasicDBObject fieldsObject = new BasicDBObject();
        fieldsObject.put("name", 1);
        Query query = new BasicQuery(dbObject.toJson(), fieldsObject.toJson());
        query.with(PageRequest.of(0, 10, Sort.Direction.ASC, "createDate"));
        return mongoTemplate.find(query, SchemaDoc.class);
    }

    public List<SchemaDoc> getSchemaDocByName(String name) {
        return schemaDocRepository.findByNameContains(name);
    }

    /**
     * @param id
     * @param updateDTO
     */
    @Override
    public void update(String id, UpdateSchemaDocDTO updateDTO) {
        SchemaDoc schemaDoc = schemaDocRepository.findById(id).get();
        if (schemaDoc != null) {
            BeanUtils.copyProperties(updateDTO, schemaDoc);
            schemaDocRepository.save(schemaDoc);
        } else {
            throw new MyException(ResultEnum.NO_OBJECT);
        }
    }
}
