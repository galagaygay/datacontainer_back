package njnu.opengms.container.service;

import njnu.opengms.container.dto.refactormethod.AddRefactorMethodDTO;
import njnu.opengms.container.dto.refactormethod.FindRefactorMethodDTO;
import njnu.opengms.container.dto.refactormethod.UpdateRefactorMethodDTO;
import njnu.opengms.container.enums.ResultEnum;
import njnu.opengms.container.exception.MyException;
import njnu.opengms.container.pojo.RefactorMethod;
import njnu.opengms.container.repository.RefactorMethodRepository;
import njnu.opengms.container.service.common.BaseService;
import njnu.opengms.container.vo.RefactorMethodVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName RefactorMethodService
 * @Description todo
 * @Author sun_liber
 * @Date 2018/12/17
 * @Version 1.0.0
 */
@Service
public class RefactorMethodServiceImp implements BaseService<RefactorMethod, AddRefactorMethodDTO, UpdateRefactorMethodDTO, FindRefactorMethodDTO, RefactorMethodVO, String> {

    @Autowired
    RefactorMethodRepository refactorMethodRepository;

    public List<RefactorMethod> findBySchema(String id) {
        return refactorMethodRepository.findBySupportedUdxSchemas(id);
    }

    @Override
    public void add(AddRefactorMethodDTO addDTO) {
        if (refactorMethodRepository.findByName(addDTO.getName()) != null) {
            throw new MyException(ResultEnum.EXIST_OBJECT);
        }
        RefactorMethod refactorMethod = new RefactorMethod();
        BeanUtils.copyProperties(addDTO, refactorMethod);
        refactorMethod.setCreateDate(new Date());
        refactorMethodRepository.save(refactorMethod);
    }

    @Override
    public void remove(String id) {
        refactorMethodRepository.deleteById(id);
    }

    @Override
    public Page<RefactorMethodVO> list(FindRefactorMethodDTO findDTO) {
        RefactorMethod refactorMethod = new RefactorMethod();
        BeanUtils.copyProperties(findDTO, refactorMethod);

        // 只包含字符串的开始、结束、包含和正则匹配，以及其他字段的精确匹配
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("description", match -> match.contains().ignoreCase())
                .withMatcher("name", match -> match.contains().ignoreCase())
                .withIncludeNullValues();

        Example<RefactorMethod> refactorMethodExample = Example.of(refactorMethod, matcher);

        PageRequest pageRequest;
        if (findDTO.getProperties() == null) {
            //不排序
            pageRequest = PageRequest.of(findDTO.getPage() - 1, findDTO.getPageSize());
        } else {
            //排序
            Sort sort = new Sort(findDTO.getAsc() ? Sort.Direction.ASC : Sort.Direction.DESC, findDTO.getProperties());
            pageRequest = PageRequest.of(findDTO.getPage() - 1, findDTO.getPageSize(), sort);
        }
        Page<RefactorMethod> page = refactorMethodRepository.findAll(refactorMethodExample, pageRequest);
        List<RefactorMethodVO> listVO = new ArrayList<>();
        page.getContent().forEach(el -> {
            RefactorMethodVO vo = new RefactorMethodVO();
            BeanUtils.copyProperties(el, vo);
            listVO.add(vo);
        });
        Page<RefactorMethodVO> pageVo = new PageImpl<>(listVO, page.getPageable(), page.getTotalElements());
        return pageVo;
    }

    @Override
    public RefactorMethod get(String s) {
        return refactorMethodRepository.findById(s).orElseGet(() -> {
            System.out.println("有人乱查数据库！！");
            throw new MyException(ResultEnum.NO_OBJECT);
        });
    }

    @Override
    public RefactorMethod getByExample(RefactorMethod refactorMethod) {
        return refactorMethodRepository.findOne(Example.of(refactorMethod)).orElseGet(() -> {
            System.out.println("有人乱查数据库！！");
            throw new MyException(ResultEnum.NO_OBJECT);
        });
    }

    @Override
    public long count() {
        return refactorMethodRepository.count();
    }

    @Override
    public void update(String id, UpdateRefactorMethodDTO updateDTO) {
        RefactorMethod refactorMethod = refactorMethodRepository.findById(id).orElseGet(() -> {
            System.out.println("有人乱查数据库！！");
            throw new MyException(ResultEnum.NO_OBJECT);
        });

            BeanUtils.copyProperties(updateDTO, refactorMethod);
            refactorMethodRepository.save(refactorMethod);


    }
}
