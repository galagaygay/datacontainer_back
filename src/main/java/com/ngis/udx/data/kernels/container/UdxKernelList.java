package com.ngis.udx.data.kernels.container;

import com.ngis.udx.EType;
import com.ngis.udx.data.UdxKernel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @ClassName UdxKernelList
 * @Description todo
 * @Author sun_liber
 * @Date 2018/12/13
 * @Version 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
public class UdxKernelList implements UdxKernel {
    final EType eType = EType.ETYPE_LIST;

    @Override
    public EType returnEType() {
        return eType;
    }
}
