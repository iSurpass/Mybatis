/*package bmatch.tech.hello.web;

import bmatch.tech.hello.dao.CommonCityCodeMapper;
import bmatch.tech.hello.dataobject.CommonCityCode;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.awt.*;

@Mapper
@Component
@Path("city")
public class CityRescource {

    @Autowired
    CommonCityCodeMapper codeMapper;

    @GET
    @Produces(MediaType.APPLICATION_JSON+";charset=utf-8")
    public CommonCityCode get(){
        List<CommonCityCode> rs = codeMapper.selectAll();

        return rs.get(0);
    }
}
*/