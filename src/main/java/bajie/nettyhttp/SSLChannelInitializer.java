package bajie.nettyhttp;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLEngine;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.logging.SocketHandler;

//构建Https，
public class SSLChannelInitializer extends ChannelInitializer<SocketChannel> {

    private SslContext sslContext;

    // 构造方法
    public SSLChannelInitializer(){
        String keyStoreFilePath = "/root/.ssl/test.pkcs12";
        String keyStorePassword = "Password@123";

        try{
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(new FileInputStream(keyStoreFilePath),keyStorePassword.toCharArray());

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore,keyStorePassword.toCharArray());

            sslContext = SslContextBuilder.forServer(keyManagerFactory).build();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        SSLEngine sslEngine = sslContext.newEngine(ch.alloc());
        pipeline
                .addLast(new SslHandler(sslEngine))
                .addLast("decoder", new HttpRequestDecoder())
                .addLast("encoder", new HttpResponseEncoder())
                .addLast("aggregator", new HttpObjectAggregator(512 * 1024))
                .addLast("handler", new HttpHandler());
        ;
    }
}
