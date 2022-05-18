package io.barth.book;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import io.barth.userbook.UserBooks;
import io.barth.userbook.UserBooksPrimaryKey;
import io.barth.userbook.UserBooksRepository;

@Controller
public class BookController {

    private final String coverImgageRoot = "http://covers.openlibrary.org/b/id/";

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserBooksRepository userBooksRepository;

    @GetMapping("/books/{bookId}")
    public String getBook(@PathVariable String bookId, Model model, @AuthenticationPrincipal OAuth2User principal) {

        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            String imageURL = "/images/no-image.png";
            if (book.getCoverIds() != null & book.getCoverIds().size() > 0) {
                imageURL = coverImgageRoot + book.getCoverIds().get(0) + "-L.jpg";

            }
            model.addAttribute("coverImage", imageURL);
            model.addAttribute("book", book);
            if (principal != null && principal.getAttribute("login") != null) {
                String userId = principal.getAttribute("login");
                model.addAttribute("loginId", userId);

                UserBooksPrimaryKey key = new UserBooksPrimaryKey();
                key.setBookId(bookId);
                key.setUserId(userId);

                Optional<UserBooks> userBooks = userBooksRepository.findById(key);
                if (userBooks.isPresent()) {
                    model.addAttribute("userBooks", userBooks.get());
                } else {
                    model.addAttribute("userBooks", new UserBooks());
                }
            }
            return "book";
        }
        return "book-not-found";
    }
}
