package bmatch.tech.hello.web;

import bmatch.tech.hello.dao.FileMapper;
import bmatch.tech.hello.dao.FolderMapper;
import bmatch.tech.hello.dataobject.File;
import bmatch.tech.hello.dataobject.Folder;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Component
@Path("file")
public class FileRescource {

    @Autowired
    FileMapper fileMapper;
    @Autowired
    FolderMapper folderMapper;

    private static void isFile(java.io.File file){
        if(!file.exists()){
            if(!file.getParentFile().exists()){
                file.getParentFile().mkdir();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public String upload(@FormDataParam("file")InputStream fileInputstream,
                         @FormDataParam("file")FormDataContentDisposition formDataContentDisposition,
                         @QueryParam("folder")String folder,
                         @QueryParam("id") int id){

        String name = formDataContentDisposition.getFileName();
        String type = name.substring(name.lastIndexOf(".")+1);
        //判断文件夹下是否存在相同名数据
        File similar = fileMapper.selectByPrimaryKey(id);
        if(Objects.nonNull(similar)){
            return "目录下存在同名的文件";
        }
        //如果文件夹在文件夹表中不存在则重新创建
        List<Folder> folders = folderMapper.selectAll();
        folders = folders.stream().filter(folder1 -> folder1.getName().equals(name)).collect(Collectors.toList());
        int a = 0;
        if(folders.size() == 0){
            Folder newFolder = new Folder();
            newFolder.setName(name);
            newFolder.setId(id);
            newFolder.setGmtcreated(new Date());
            newFolder.setGmtmodified(new Date());
            a = folderMapper.insert(newFolder);
        }
        if(!(a==1)){
            return "新建文件夹失败";
        }

        File file = new File();
        file.setName(name);
        file.setFoldername(folder);
        file.setId(id);
        file.setGmtcreated(new Date());
        file.setGmtmodified(new Date());

        //创建新文件对象
        java.io.File uploadFile = new java.io.File("/upload/"+folder+"/"+name);
        isFile(uploadFile);

        //上传文件
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(uploadFile);
            int len = -1;
            byte[] bytes = new byte[1024];
            while ((len = fileInputstream.read(bytes))!=-1){
                fileOutputStream.write(bytes,0,len);
            }

            int c = fileMapper.insert(file);
            if(c==1){
                return "文件上传成功";
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "上传失败";
    }

    @GET
    @Path("/download")
    @Produces(MediaType.MULTIPART_FORM_DATA)
    public String download(@DefaultValue("")@QueryParam("name")String name,
                           @DefaultValue("")@QueryParam("folder")String folder,
                           @DefaultValue("")@QueryParam("downloadPath")String downloadPath,
                           @QueryParam("id")int id){
        List<File> files = fileMapper.selectAll().stream().filter(file -> file.getName().equals(name)).collect(Collectors.toList());
        if(files.size()==0){
            return "该文件不存在";
        }
        java.io.File res = new java.io.File("/upload/"+folder+"/"+name);
        java.io.File des = new java.io.File(downloadPath+ java.io.File.separator+name+"."+files.get(0).getType());
        try {
            //模拟文件下载
            FileInputStream in = new FileInputStream(res);
            FileOutputStream out = new FileOutputStream(des);
            int len = -1;
            byte[] bytes = new byte[1024];
            while ((len = in.read(bytes))!=-1){
                out.write(bytes,0,len);
            }
            return "文件下载成功";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "文件下载失败";
    }

    @PUT
    @Path("/update")
    @Produces(MediaType.APPLICATION_JSON)
    public String update(@DefaultValue("")@QueryParam("oldName")String oldName,
                         @DefaultValue("")@QueryParam("newName")String newName,
                         @QueryParam("id")int id) {

        List<File> files = fileMapper.selectAll();
        files = files.stream().filter(file -> file.getName().equals(oldName)
                && file.getId().equals(id)).collect(Collectors.toList());
        if (files.size() == 0) {
            return "文件重名更新失败";
        }

        java.io.File oldFile = new java.io.File("/upload/" + files.get(0).getFoldername() + java.io.File.separator + oldName);
        java.io.File newFile = new java.io.File("/upload/" + files.get(0).getFoldername() + java.io.File.separator + newName+"."+files.get(0).getType());

        boolean Flag = oldFile.renameTo(newFile);
        int deleteFlag = 0;
        File desFile = null;
        if (Flag) {
            deleteFlag = fileMapper.deleteByPrimaryKey(id);
            desFile = files.get(0);
            desFile.setId(id);
            desFile.setName(newName);
            desFile.setGmtmodified(new Date());
        }
        if (deleteFlag != 0) {
            return "更新成功";
        }
        return "更新失败";
    }

    @DELETE
    @Path("/delete")
    public String delete(@DefaultValue("")@QueryParam("name")String name,
                         @DefaultValue("")@QueryParam("folder")String folder,
                         @QueryParam("id")int id){
        List<File> files = fileMapper.selectAll();
        files = files.stream().filter(file -> file.getName().equals(name) &&
                file.getFoldername().equals(folder)).collect(Collectors.toList());
        if(files.size() == 0){
            return "无该文件名 删除失败";
        }
        java.io.File del = new java.io.File("/upload/" + files.get(0).getFoldername() + java.io.File.separator +
                                            name +"."+files.get(0).getType());
        boolean delFlag1 = del.delete();
        int delFlag2 = fileMapper.deleteByPrimaryKey(id);
        if(delFlag1 && delFlag2==1 ){
            return "删除成功!";
        }
        return "删除失败";
    }

    @GET
    @Path("/showFolder")
    @Produces(MediaType.APPLICATION_JSON)
    public List<File> showFolder(@DefaultValue("")@QueryParam("folderName")String folderName,
                                 @QueryParam("id")int id){
        List<Folder> folders = folderMapper.selectAll();
        folders = folders.stream().filter(folder -> folder.getName().equals(folderName)&&
                                          folder.getId().equals(id)).collect(Collectors.toList());
        if(folders.size() == 0){
            return null;
        }
        List<File> fileList = fileMapper.selectAll();
        fileList = fileList.stream().filter(file -> file.getFoldername().equals(folderName)).collect(Collectors.toList());
        if(fileList.size() == 0){
            return null;
        }
        return fileList;
    }


}
