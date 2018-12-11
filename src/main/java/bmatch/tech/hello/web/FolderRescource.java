package bmatch.tech.hello.web;

import bmatch.tech.hello.dao.FileMapper;
import bmatch.tech.hello.dao.FolderMapper;
import bmatch.tech.hello.dataobject.File;
import bmatch.tech.hello.dataobject.Folder;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Pattern;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Mapper
@Path("folder")
public class FolderRescource {

    @Autowired
    FileMapper fileMapper;
    @Autowired
    FolderMapper folderMapper;

    @GET
    @Path("/create")
    public String create(@QueryParam("id")  int id,
                         @QueryParam("name") String name){

        folderMapper = (FolderMapper) folderMapper.selectByPrimaryKey(id);
        if(!Objects.isNull(folderMapper)){
            return "该文件夹ID已存在";
        }
        Folder folder = new Folder();
        folder.setId(id);
        folder.setName(name);
        folder.setGmtcreated(new Date());
        folder.setGmtmodified(new Date());
        int flag = folderMapper.insert(folder);
        if(flag == 1){
            return "文件夹" + name + "创建成功！";
        }
        return "文件夹创建失败!";
    }

    @GET
    @Path("/show")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Folder> show(@QueryParam("id") int id){

        Folder folder = folderMapper.selectByPrimaryKey(id);
        if(Objects.isNull(folder)){
            System.out.println("该文件夹id不存在");
        }
        List<Folder> folders = folderMapper.selectAll();
        folders = folders.stream().filter(folder1 -> folder.getId().equals(id)).collect(Collectors.toList());
        if (folders.size() == 0){
            System.out.println("该文件夹下不存在");
        }
        return folders;
    }

    @DELETE
    @Path("/delete")
    public String delete(@DefaultValue("")@QueryParam("name")String name,
                         @QueryParam("id")int id){
        List<Folder> folders = folderMapper.selectAll();
        folders = folders.stream().filter(folder -> folder.getName().equals(name)&&
                folder.getId().equals(id)).collect(Collectors.toList());
        if(folders.size() == 0){
            return "无该文件夹 更新失败";
        }

        List<File> files = fileMapper.selectAll();
        files = files.stream().filter(file -> file.getFoldername().equals(name)).collect(Collectors.toList());
        if(files.size() == 0){
            java.io.File delFile = new java.io.File("/upload/" + name);
            delFile.delete();
            folderMapper.deleteByPrimaryKey(id);
            return "删除成功!";
        }
        for (File file:files){
            java.io.File del = new java.io.File("/upload/"+file.getFoldername()+
                                                java.io.File.separator+file.getType());
            del.delete();
            fileMapper.deleteByPrimaryKey(file.getId());
            return "删除成功!";
        }
        return "删除失败!";
    }

    @GET
    @Path("/rename")
    public String rename(@DefaultValue ("")@QueryParam("oldFolderName")String oldFolderName,
                         @DefaultValue("")@QueryParam("newFolderName")String newFolderName,
                         @QueryParam("id")int id){

        List<Folder> folders = folderMapper.selectAll();
        folders = folders.stream().filter(folder -> folder.getName().equals(oldFolderName)&&
                folder.getId().equals(id)).collect(Collectors.toList());
        if(folders.size() == 0){
            return "无该文件夹 更新失败";
        }
        //判断该文件夹下有无文件还是多文件
        List<File> files = fileMapper.selectAll();
        files = files.stream().filter(file -> file.getFoldername().equals(oldFolderName)).collect(Collectors.toList());

        //若是无文件时
        if (files.size() == 0){
            java.io.File del = new java.io.File("/upload/"+oldFolderName);
            java.io.File upd = new java.io.File("/upload/"+newFolderName);
            boolean flag = del.renameTo(upd);
            if(flag){
                int mark = folderMapper.deleteByPrimaryKey(id);
                if(mark != 1){
                    return "重命名失败";
                }
                //创建新对象并插入新表
                Folder folder = new Folder();
                folder.setName(newFolderName);
                int flag1 = folderMapper.insert(folder);
                if(flag1 !=1){
                    return "重命名失败";
                }
                return "重命名成功";
            }
            return "重命名失败";
        }

        java.io.File del = new java.io.File("/upload/"+oldFolderName);
        java.io.File upd = new java.io.File("/upload/"+newFolderName);
        boolean flag = del.renameTo(upd);
        //若是多文件时 遍历创建更新插入
        if(flag){
            for(File file:files){
                int flag1 = fileMapper.deleteByPrimaryKey(id);
                if(flag1!=1){
                    return "重命名失败";
                }
                File file1 = new File();
                file1.setFoldername(newFolderName);
                int flag2 = fileMapper.insert(file1);
                if(flag2 != 1){
                    return "重命名失败";
                }
            }
            //文件夹在FileDelete
            int mark = folderMapper.deleteByPrimaryKey(id);
            if(mark != 1){
                return "重命名失败";
            }
            //创建新对象并插入新表
            Folder folder = new Folder();
            folder.setName(newFolderName);
            int flag1 = folderMapper.insert(folder);
            if(flag1 !=1){
                return "重命名失败";
            }
            return "重命名成功";
        }
        return "重命名失败";
    }
}
