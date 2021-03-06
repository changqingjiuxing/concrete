package cc.coodex.practice.jaxrs.impl;

import cc.coodex.concrete.attachments.client.ClientServiceImpl;
import cc.coodex.concrete.common.ConcreteException;
import cc.coodex.concrete.common.ErrorCodes;
import cc.coodex.concrete.common.Token;
import cc.coodex.concrete.core.token.TokenWrapper;
import cc.coodex.practice.jaxrs.api.ServiceExample;
import cc.coodex.practice.jaxrs.pojo.Book;
import cc.coodex.practice.jaxrs.pojo.BookInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by davidoff shen on 2016-11-28.
 */
@Named
public class ServiceExampleImpl implements ServiceExample {

    private final static Logger log = LoggerFactory.getLogger(ServiceExampleImpl.class);

    private Token token = TokenWrapper.getInstance();

    private List<Book> books = new ArrayList<Book>();

    public ServiceExampleImpl() {
        books.add(new Book("TSP培训开发团队", "Watts S. Humplhrey", 5900));
        books.add(new Book("从0到1  开启商业与未来的秘密", "Peter Thiel", 4500));
        books.add(new Book("Node.js实战", "Make Cantelon/ Marc Harter/ T. J. Holowarychunk/ Nathan Rajlich", 6900));
    }

    @Override
    public List<Book> all() {
        token.setAttribute("test", "WWWWWWWWWWWWWWWW");
        log.debug("all : tokeId {}", token.getTokenId());
        return books;
    }

    @Override
    public List<Book> findByAuthorLike(String author) {
        List<Book> books = new ArrayList<Book>();
        for (Book book : this.books) {
            if (book.getAuthor().contains(author)) {
                books.add(book);
            }
        }
        return books;
    }

    @Override
    public List<Book> findByPriceLessThen(int price) {
        List<Book> books = new ArrayList<Book>();
        for (Book book : this.books) {
            if (book.getPrice() < price) {
                books.add(book);
            }
        }
        return books;
    }

    @Override
    public Book get(long bookId) {
//        List<Book> books = new ArrayList<>();
        for (Book book : this.books) {
            if (book.getId() == bookId) {
                return book;
            }
        }
        return null;
    }

    @Override
    public Book get(String author, long price) {
        log.debug("author: {}, price: {}", author, price);
        return books.get(0);
    }

    @Override
    public String update(long bookId, BookInfo book) {
        log.debug("bookId: {}, bookInfo: {}", bookId, book);
        return String.valueOf(bookId);
    }

    @Override
    public String delete(long bookId) {
        log.debug("delete book: {} ,tokenId: {}, attr test: {}", bookId, token.getTokenId(), token.getAttribute("test"));
        return String.valueOf(bookId);
    }

    @Override
    public String checkRole() {
        throw new ConcreteException(ErrorCodes.NO_AUTHORIZATION);
    }

    @Override
    public String tokenId() {
        ClientServiceImpl.allowWrite();
        return token.getTokenId();
    }
}
