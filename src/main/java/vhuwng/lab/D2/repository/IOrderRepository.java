package vhuwng.lab.D2.repository;

import java.util.List;

import vhuwng.lab.D2.dto.OrderDetailDto;
import vhuwng.lab.D2.dto.SampleQueryFilterDto;

public interface IOrderRepository {

    List<OrderDetailDto> getAllOrdersWithOptionalFilter(SampleQueryFilterDto filter);
}
