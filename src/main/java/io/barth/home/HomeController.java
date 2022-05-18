package io.barth.home;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import io.barth.user.BooksByUser;
import io.barth.user.BooksByUserRepository;

@Controller
public class HomeController {

    private final String coverImgageRoot = "http://covers.openlibrary.org/b/id/";

    @Autowired
    private BooksByUserRepository booksByUserRepository;

    @GetMapping("/")
    public String home(@AuthenticationPrincipal OAuth2User principal, Model model) {

        if (principal == null || principal.getAttribute("login") == null) {
            return "index";
        } else {
            String userId = principal.getAttribute("login");
            Slice<BooksByUser> booksSlice = booksByUserRepository.findAllById(userId, CassandraPageRequest.of(0, 20));
            List<BooksByUser> booksByUser = booksSlice.getContent();
            model.addAttribute("books", booksByUser);
            booksByUser = booksByUser.stream().distinct().map(book -> {
                String imageURL = "/images/no-image.png";
                if (book.getCoverIds() != null & book.getCoverIds().size() > 0) {
                    imageURL = coverImgageRoot + book.getCoverIds().get(0) + "-L.jpg";
                }
                book.setCoverUrl(imageURL);
                return book;
            }).collect(Collectors.toList());
            return "home";
        }

    }
}
