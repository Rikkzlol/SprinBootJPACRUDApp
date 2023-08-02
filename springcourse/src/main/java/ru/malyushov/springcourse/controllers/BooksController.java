package ru.malyushov.springcourse.controllers;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.malyushov.springcourse.models.Book;
import ru.malyushov.springcourse.models.Person;
import ru.malyushov.springcourse.service.BooksService;
import ru.malyushov.springcourse.service.PeopleService;

import java.util.List;

@Controller
@RequestMapping("/books")
public class BooksController {

    private PeopleService peopleService;

    private BooksService booksService;

    @Autowired
    public BooksController(BooksService booksService, PeopleService peopleService) {
        this.booksService = booksService;
        this.peopleService = peopleService;
    }

    @GetMapping
    public String index(Model model, @RequestParam(value = "page", required = false) Integer page,
                        @RequestParam(value = "books_per_page", required = false) Integer booksPerPage,
                        @RequestParam(value = "sort_by_year",required = false) boolean sortByYear) {
        if( page == null || booksPerPage == null)
            model.addAttribute("books", booksService.findAll(sortByYear));

        else
            model.addAttribute("books", booksService.findWithPagination(page, booksPerPage, sortByYear));

        return "/books/index";
    }
    @GetMapping("/new")
    public String newBook(Model model){
        model.addAttribute("book",new Book());
        return "books/new";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model, @ModelAttribute("person") Person person){
        model.addAttribute("book",booksService.findOne(id));

        Person bookOwner = booksService.getBookOwner(id);
        if(bookOwner != null){
            model.addAttribute("owner",bookOwner);
        } else {
            model.addAttribute("person", peopleService.findAll());
        }
        return "books/show";

    }

    @PostMapping
    public String create(@ModelAttribute("book") @Valid Book book){
        booksService.save(book);
        return "redirect:/books";

    }
    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id){
        model.addAttribute("book", booksService.findOne(id));
        return "/books/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("book") @Valid  Book book, BindingResult bindingResult, @PathVariable("id") int id){
        if(bindingResult.hasErrors())
            return "/books/edit";
        booksService.update(id, book);
        return "redirect:/books";
    }
    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id){
        booksService.delete(id);
        return "redirect:/books";
    }
    @PatchMapping("/{id}/release")
    public String release(@PathVariable("id") int id){
        booksService.release(id);
        return "redirect:/books/" + id;
    }

    @PatchMapping("/{id}/assign")
    public String assign(@PathVariable("id") int id, @ModelAttribute("person") Person selectedPerson){
        booksService.assign(id, selectedPerson);
        return "redirect:/books/" + id;
    }
}