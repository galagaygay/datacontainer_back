package njnu.opengms.container.service;

import njnu.opengms.container.dto.mappingmethod.AddMappingMethodDTO;
import njnu.opengms.container.dto.mappingmethod.FindMappingMethodDTO;
import njnu.opengms.container.dto.mappingmethod.UpdateMappingMethodDTO;
import njnu.opengms.container.enums.ResultEnum;
import njnu.opengms.container.exception.MyException;
import njnu.opengms.container.pojo.MappingMethod;
import njnu.opengms.container.repository.MappingMethodRepository;
import njnu.opengms.container.service.common.BaseService;
import njnu.opengms.container.vo.MappingMethodVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName MappingMethodService
 * @Description todo
 * @Author sun_liber
 * @Date 2018/12/17
 * @Version 1.0.0
 */
@Service
public class MappingMethodServiceImp implements BaseService<MappingMethod, AddMappingMethodDTO, UpdateMappingMethodDTO, FindMappingMethodDTO, MappingMethodVO, String> {

    @Autowired
    MappingMethodRepository mappingMethodRepository;


    public List<MappingMethod> findBySchema(String id) {
        return mappingMethodRepository.findBySupportedUdxSchema(id);
    }


    @Override
    public void add(AddMappingMethodDTO addDTO) {
        if (mappingMethodRepository.findByName(addDTO.getName()) != null) {
            throw new MyException(ResultEnum.EXIST_OBJECT);
        }
        MappingMethod mappingMethod = new MappingMethod();
        BeanUtils.copyProperties(addDTO, mappingMethod);
        mappingMethod.setCreateDate(new Date());
        mappingMethodRepository.save(mappingMethod);
    }

    @Override
    public void remove(String id) {
        mappingMethodRepository.deleteById(id);
    }

    @Override
    public Page<MappingMethodVO> list(FindMappingMethodDTO findDTO) {
        MappingMethod mappingMethod = new MappingMethod();
        BeanUtils.copyProperties(findDTO, mappingMethod);

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("description", match -> match.contains().ignoreCase())
                .withMatcher("name", match -> match.contains().ignoreCase())
                .withIncludeNullValues();

        Example<MappingMethod> mappingMethodExample = Example.of(mappingMethod, matcher);

        PageRequest pageRequest;
        if (findDTO.getProperties() == null) {
            //不排序
            pageRequest = PageRequest.of(findDTO.getPage() - 1, findDTO.getPageSize());
        } else {
            //排序
            Sort sort = new Sort(findDTO.getAsc() ? Sort.Direction.ASC : Sort.Direction.DESC, findDTO.getProperties());
            pageRequest = PageRequest.of(findDTO.getPage() - 1, findDTO.getPageSize(), sort);
        }

        Page<MappingMethod> page = mappingMethodRepository.findAll(mappingMethodExample, pageRequest);


        List<MappingMethodVO> listVO = new ArrayList<>();

        page.getContent().forEach(el -> {
            MappingMethodVO vo = new MappingMethodVO();
            BeanUtils.copyProperties(el, vo);
            listVO.add(vo);
        });
        Page<MappingMethodVO> pageVo = new PageImpl<>(listVO, page.getPageable(), page.getTotalElements());
        return pageVo;
    }

    @Override
    public MappingMethod get(String s) {
        return mappingMethodRepository.findById(s).orElseGet(() -> {
            System.out.println("有人乱查数据库！！");
            throw new MyException(ResultEnum.NO_OBJECT);
        });
    }

    @Override
    public MappingMethod getByExample(MappingMethod mappingMethod) {
        return mappingMethodRepository.findOne(Example.of(mappingMethod)).orElseGet(() -> {
            System.out.println("有人乱查数据库！！");
            throw new MyException(ResultEnum.NO_OBJECT);
        });
    }

    @Override
    public long count() {
        return mappingMethodRepository.count();
    }

    @Override
    public void update(String id, UpdateMappingMethodDTO updateDTO) {
        MappingMethod mappingMethod = mappingMethodRepository.findById(id).orElseGet(() -> {
            System.out.println("有人乱查数据库！！");
            throw new MyException(ResultEnum.NO_OBJECT);
        });

            BeanUtils.copyProperties(updateDTO, mappingMethod);
            mappingMethodRepository.save(mappingMethod);

    }
}
