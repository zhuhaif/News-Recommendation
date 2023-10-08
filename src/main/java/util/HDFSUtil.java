package com.neu.hdfs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.io.IOUtils;

public class HDFSUtil {

	private FileSystem fs;

	private Configuration conf;

	public HDFSUtil() throws IOException {
		conf = new Configuration();
		fs = FileSystem.get(conf);
	}

	/**
	 * 创建HDFS目录
	 *
	 * @param path
	 *            创建的目录路径
	 * @return true:创建成功
	 * @throws IOException
	 */
	public boolean mkdir(String path) throws IOException {
		Path srcPath = new Path(path);
		return fs.mkdirs(srcPath);
	}

	/**
	 * 递归删除HDFS目录
	 *
	 * @param path 要删除的HDFS目录
	 * @return true:删除成功
	 * @throws IOException
	 */
	public boolean clearDir(String path) throws IOException{
		Path srcPath = new Path(path);
		return fs.delete(srcPath, true);
	}

	/**
	 * 上传本地文件到HDFS
	 *
	 * @param src
	 *            源文件路径
	 * @param dst
	 *            HDFS路径（目标路径）
	 * @throws IOException
	 */
	public void put(String src, String dst, boolean delSrc, boolean overwrited) throws IOException {
		Path srcPath = new Path(src);
		Path dstPath = new Path(dst); // 目标路径
		// 调用文件系统的文件复制函数,前面参数是指是否删除原文件，true为删除，默认>为false
		fs.copyFromLocalFile(delSrc, overwrited, srcPath, dstPath);
		// FileUtil.copy(new File(src), fs, dstPath, false, conf);
	}


	/**
	 * 修改HDFS目录的读写权限
	 *
	 * @param src 要修改的HDFS目录
	 * @param mode 读写权限(例如：744)
	 * @throws IOException
	 */
	public void changePermission(Path src, String mode) throws IOException {
		//Path path = new Path(src);
		FsPermission fp = new FsPermission(mode);
		fs.setPermission(src, fp);
	}

	/**
	 * 上传本地文件到HDFS
	 *
	 * @param src
	 *            源文件路径
	 * @param dst
	 *            HDFS路径（目标路径）
	 * @throws IOException
	 */
	public void get(String src, String dst) throws IOException {
		Path srcPath = new Path(src);
		Path dstPath = new Path(dst); // 目标路径
		fs.copyToLocalFile(srcPath, dstPath);
		// FileUtil.copy(new File(src), fs, dstPath, false, conf);
	}

	/**
	 * 校验文件是否存在于HDFS中。
	 *
	 * @param filePath
	 *            文件再HDFS中的路径
	 * @return true：存在
	 * @throws IOException
	 */
	public boolean check(String filePath) throws IOException {
		Path path = new Path(filePath);
		boolean isExists = fs.exists(path);
		return isExists;
	}

	/**
	 * 向HDFS文件中追加内容
	 *
	 * @param filePath 文件路径
	 * @return 是否追加成功。
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public boolean appendContent(InputStream in, String filePath) throws IOException {
		conf.setBoolean("dfs.support.append", true);
		if(!check(filePath)) {
			fs.createNewFile(new Path(filePath));
		}
		//要追加的文件流，inpath为文件
		OutputStream out = fs.append(new Path(filePath));
		IOUtils.copyBytes(in, out, 10, true);
		in.close();
		out.close();
		fs.close();
		return false;
	}

	/**
	 * 查看指定目录下的所有文件
	 * @param filePath HDFS目录路径
	 * @return 指定路径下所有文件列表
	 * @throws IOException
	 */
	public List<String> listFile(String filePath, String ext) throws IOException {
		List<String> listDir = new ArrayList<String>();
		Path path = new Path(filePath);
		RemoteIterator<LocatedFileStatus> it = fs.listFiles(path, true);
		while(it.hasNext()) {
			String name = it.next().getPath().toString();
			if(name.endsWith(ext)) {
				listDir.add(name);
			}
		}
		return listDir;
	}

	/**
	 * 得到一个目录(不包括子目录)下的所有名字匹配上pattern的文件名
	 *
	 * @param fs
	 * @param folderPath
	 * @param pattern
	 *            用于匹配文件名的正则
	 * @return
	 * @throws IOException
	 */
	public List<Path> getFilesUnderFolder(FileSystem fs, Path folderPath,
										  String pattern) throws IOException {
		List<Path> paths = new ArrayList<Path>();
		if (fs.exists(folderPath)) {
			FileStatus[] fileStatus = fs.listStatus(folderPath);
			for (int i = 0; i < fileStatus.length; i++) {
				FileStatus fileStatu = fileStatus[i];
				// 如果是目录，递归向下找
				if (fileStatu.isDir()) {
					Path oneFilePath = fileStatu.getPath();
					if (pattern == null) {
						paths.add(oneFilePath);
					} else {
						if (oneFilePath.getName().contains(pattern)) {
							paths.add(oneFilePath);
						}
					}
				}
			}
		}
		return paths;
	}

	/**
	 * 移动HDFS文件到另外一个目录
	 * @param src 源文件
	 * @param dest 目标文件
	 * @throws IOException
	 */
	public void moveFile(String src, String dest) throws IOException{
		Path srcPath = new Path(src);
		Path destPath = new Path(dest);
//		if(this.check(dest)) {
//			log.error("文件" + dest + "已经存在，无法覆盖！");
//			return;
//		}
		fs.rename(srcPath, destPath);
	}

	/**
	 * 移动HDFS文件到另外一个目录
	 * @param src 源文件
	 * @param dest 目标文件
	 * @throws IOException
	 */
	public void copyFile(String src, String dest) throws IOException{
		Path srcPath = new Path(src);
		Path destPath = new Path(dest);
//		if(this.check(dest)) {
//			log.error("文件" + dest + "已经存在，无法覆盖！");
//			return;
//		}
		fs.rename(srcPath, destPath);
	}

	public OutputStream getHDFSOutputStream(String path) throws IllegalArgumentException, IOException {
		FSDataOutputStream dataOutStream = null;
		dataOutStream = fs.create(new Path(path), true, 1024*1024*1024);
		return dataOutStream;
	}

	// 释放资源
	public void destroy() throws IOException {
		if (fs != null)
			fs.close();
	}

	public FileSystem getFs() {
		return fs;
	}

	public void setFs(FileSystem fs) {
		this.fs = fs;
	}

	public static void main(String[] args) {
		String srcPath = "d:\\20150101-20150201.csv";
		String descPath = "/out/test.csv";
		// 创建文件夹
		try {
			HDFSUtil hdfsAPI = new HDFSUtil();
			// hdfsAPI.simpleUpLoad(srcPath, descPath);
//			 hdfsAPI.mkdir("/user/hadoop/test/askd123.bz2");
			// hdfsAPI.put(srcPath, descPath);
			// hdfsAPI.check("/user/hadoop/test/config.properties");
//			 List<String> fileList = hdfsAPI.listFile("hdfs://idh104:8020//apps/hive/warehouse/mc10_new", "2015.bz2");
//			 for (String str : fileList) {
//				 System.out.println(str);
//			 }
//			List<Path> pathList = hdfsAPI.getFilesUnderFolder(hdfsAPI.getFs(),
//					new Path("/dataAnalysis"), "");
//			System.out.println(pathList.size());
			//hdfsAPI.clearDir("/dataAnalysis/tmp");

			hdfsAPI.listFile("hdfs://idh104:8020/user/zhangx/", "csv");

			//hdfsAPI.get("hdfs://idh13:8020/dataAnalysis/gpc.csv", "D:\\gpc.csv");
			//hdfsAPI.changePermission("/user/hadoop/test/askd123.bz2","777");
//			BufferedInputStream bi = new BufferedInputStream(new ByteArrayInputStream("test11111".getBytes()));
//			hdfsAPI.appendContent(bi, "/dataAnalysis/duliming/input/test1.csv");
//			hdfsAPI.put("D:\\TEST\\fc=8.csv", "/dataAnalysis/duliming//", false, true);
//			hdfsAPI.moveFile("/apps/hive/warehouse/tenmindata1/1/WT02288/2015.bz2", "/apps/hive/warehouse/tenmindata1/1/WT02288/2018.bz2");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
