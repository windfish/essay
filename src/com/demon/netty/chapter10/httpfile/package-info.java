/**
 * Netty 的 HTTP服务端入门：文件服务器
 * 使用 HTTP协议对外提供服务，当客户端通过浏览器访问文件服务器时，
 * 对访问路径进行检查，检查失败时返回 HTTP 403错误，该页无法访问；
 * 当校验通过，以链接的方式打开当前文件目录，每个目录或者文件都是个超链接，可以递归访问；
 * 如果是目录，可以继续访问它下面的目录或文件；如果是文件且可读，则可以在浏览器直接打开或者下载该文件
 */
package com.demon.netty.chapter10.httpfile;