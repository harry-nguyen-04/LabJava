package vhuwng.lab.D3.repository;

import vhuwng.lab.D3.dto.OrderDetailDto;
import vhuwng.lab.D3.dto.SampleQueryFilterDto;

import java.util.List;

public interface IOrderRepository {

    List<OrderDetailDto> getAllOrdersWithOptionalFilter(SampleQueryFilterDto filter);
}
