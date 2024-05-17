package com.net.file.util;

import net.coobird.thumbnailator.Thumbnails;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class ImgTools {
    private static final int MAX_IMAGE_WIDTH=160;
    private static final int MAX_IMAGE_HEIGHT=160;

    /**
     * 截取文件缩略图
     * @param inputStream
     * @return {@link InputStream }
     * @throws Exception
     */
    public InputStream randomGrabberFFmpegVideoImage(InputStream inputStream) throws Exception {
        String targetFilePath = "";
        FFmpegFrameGrabber ff =new FFmpegFrameGrabber(inputStream);
        ff.start();
        //判断是否是竖屏小视频
        String rotate = ff.getVideoMetadata("rotate");
        int ffLength = ff.getLengthInFrames();
        Frame f;
        int i = 0;
        int index = 1;//截取图片第几帧
        while (i < ffLength) {
            f = ff.grabImage();
            if (i == index) {
                if (null != rotate && rotate.length() > 1) {
                    return getImageInputStream(f,true);
                } else {
                    return getImageInputStream(f,false);
                }
            }
            i++;
        }
        return null;
    }

    /**
     * 生成图片缩略图
     * @param inputStream
     * @param outputStream
     * @throws IOException
     */
    public void generateSmallerImage(InputStream inputStream,OutputStream outputStream) throws IOException {
        BufferedImage image = ImageIO.read(inputStream);
        int[] scaleSize = getScaleSize(new int[]{image.getHeight(), image.getWidth()});
        Thumbnails.of(image)
                .size(scaleSize[0],scaleSize[1])
                .outputFormat("jpg")
                .toOutputStream(outputStream);
    }
    private int[] getScaleSize(int[] size){
        double scale=1;
        scale=Math.max(size[0]*1.0/MAX_IMAGE_HEIGHT,size[1]*1.0/MAX_IMAGE_WIDTH);
        return new int[]{(int) (size[0]/scale), (int) (size[1]/scale)};
    }

    public InputStream getImageInputStream(Frame f, boolean bool) throws Exception {
        if (null == f || null == f.image) {
            return null;
        }
        Java2DFrameConverter converter = new Java2DFrameConverter();
        BufferedImage bi = converter.getBufferedImage(f);
        if (bool == true) {
            Image image = (Image) bi;
            bi = rotate(image, 90);//图片旋转90度
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(bi, "png", os);
        byte[] sdf = os.toByteArray();
        InputStream input = new ByteArrayInputStream(os.toByteArray());
        return input;
    }

    /**
     * 图片旋转角度
     *
     * @param src   源图片
     * @param angel 角度
     * @return 目标图片
     */
    public static BufferedImage rotate(Image src, int angel) {
        int src_width = src.getWidth(null);
        int src_height = src.getHeight(null);
        // calculate the new image size
        Rectangle rect_des = CalcRotatedSize(new Rectangle(new Dimension(
                src_width, src_height)), angel);

        BufferedImage res = null;
        res = new BufferedImage(rect_des.width, rect_des.height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = res.createGraphics();
        // transform(这里先平移、再旋转比较方便处理；绘图时会采用这些变化，绘图默认从画布的左上顶点开始绘画，源图片的左上顶点与画布左上顶点对齐，然后开始绘画，修改坐标原点后，绘画对应的画布起始点改变，起到平移的效果；然后旋转图片即可)

        //平移（原理修改坐标系原点，绘图起点变了，起到了平移的效果，如果作用于旋转，则为旋转中心点）
        g2.translate((rect_des.width - src_width) / 2, (rect_des.height - src_height) / 2);


        //旋转（原理transalte(dx,dy)->rotate(radians)->transalte(-dx,-dy);修改坐标系原点后，旋转90度，然后再还原坐标系原点为(0,0),但是整个坐标系已经旋转了相应的度数 ）
        g2.rotate(Math.toRadians(angel), src_width / 2, src_height / 2);

//        //先旋转（以目标区域中心点为旋转中心点，源图片左上顶点对准目标区域中心点，然后旋转）
//        g2.translate(rect_des.width/2,rect_des.height/ 2);
//        g2.rotate(Math.toRadians(angel));
//        //再平移（原点恢复到源图的左上顶点处（现在的右上顶点处），否则只能画出1/4）
//        g2.translate(-src_width/2,-src_height/2);


        g2.drawImage(src, null, null);
        return res;
    }

    /**
     * 计算转换后目标矩形的宽高
     *
     * @param src   源矩形
     * @param angel 角度
     * @return 目标矩形
     */
    private static Rectangle CalcRotatedSize(Rectangle src, int angel) {
        double cos = Math.abs(Math.cos(Math.toRadians(angel)));
        double sin = Math.abs(Math.sin(Math.toRadians(angel)));
        int des_width = (int) (src.width * cos) + (int) (src.height * sin);
        int des_height = (int) (src.height * cos) + (int) (src.width * sin);
        return new Rectangle(new Dimension(des_width, des_height));
    }
}