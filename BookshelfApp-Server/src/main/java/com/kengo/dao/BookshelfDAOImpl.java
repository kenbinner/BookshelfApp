package com.kengo.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kengo.entity.BookEntity;
import com.kengo.entity.CustomerEntity;
import com.kengo.model.Book;
import com.kengo.model.Customer;

@Transactional
@Repository(value = "bookshelfDAO")
public class BookshelfDAOImpl implements BookshelfDAO{
	
	@Autowired
	private EntityManager entityManager;

	@Override
	public List<Book> getBooks(Integer custId) throws Exception {
		
		Query q = entityManager.createQuery("Select b from BookEntity b inner join CustomerEntity c on b.customer = c.customerId where c.customerId = :custId");
		q.setParameter("custId", custId);
		List<BookEntity> bookEntityList = q.getResultList();
		List<Book> bookList = new ArrayList<Book>();
		
		if(!bookEntityList.isEmpty()){
			for(BookEntity bookEntity : bookEntityList){
				Book book = new Book();
				book.setAuthor(bookEntity.getAuthor());
				book.setComments(bookEntity.getComments());
				book.setIsbn(bookEntity.getIsbn());
				book.setStatus(bookEntity.getStatus());
				book.setRating(bookEntity.getRating());
				book.setTitle(bookEntity.getTitle());
				book.setBookId(bookEntity.getBookId());
				book.setImage(bookEntity.getImage());
				bookList.add(book);
			}
		}
		
		return bookList;
	}

	@Override
	public String addBook(Book book) throws Exception {
		BookEntity bookEntity = new BookEntity();
		bookEntity.setAuthor(book.getAuthor());
		bookEntity.setComments(book.getComments());
		bookEntity.setCustomer(book.getCustomer());
		bookEntity.setIsbn(book.getIsbn());
		bookEntity.setRating(book.getRating());
		bookEntity.setStatus(book.getStatus());
		bookEntity.setTitle(book.getTitle());
		bookEntity.setImage(book.getImage());
		entityManager.persist(bookEntity);
		return book.getTitle();
	}

	@Override
	public String removeBook(Integer bookId) throws Exception {
		BookEntity bookEntity = entityManager.find(BookEntity.class, bookId);
		entityManager.remove(bookEntity);
		return bookEntity.getTitle();
	}

	@Override
	public String updateBook(Book book) throws Exception {
		BookEntity bookEntity = entityManager.find(BookEntity.class, book.getBookId());
		bookEntity.setAuthor(book.getAuthor());
		bookEntity.setComments(book.getComments());
		bookEntity.setIsbn(book.getIsbn());
		bookEntity.setStatus(book.getStatus());
		bookEntity.setRating(book.getRating());
		bookEntity.setTitle(book.getTitle());
		bookEntity.setImage(book.getImage());
		return bookEntity.getTitle();
	}

	@Override
	public Customer authenticateCustomer(String emailId, String password) {
		Query query = entityManager.createQuery("select c from CustomerEntity c where c.email = '"+emailId+"' and c.password = '"+password+"'");
		
		List<CustomerEntity> customerEntities = query.getResultList();
		if(customerEntities.isEmpty()){
			return null;
		}else{
			CustomerEntity customerEntity = customerEntities.get(0);
			Customer customer = new Customer();
			customer.setCustomerId(customerEntity.getCustomerId());
			customer.setEmail(customerEntity.getEmail());
			customer.setName(customerEntity.getName());
			customer.setPassword(customerEntity.getPassword());
			ArrayList<Book> bookList = new ArrayList<Book>();
			for(BookEntity bookEntity : customerEntity.getBookList()){
				Book book = new Book();
				book.setAuthor(bookEntity.getAuthor());
				book.setComments(bookEntity.getComments());
				book.setIsbn(bookEntity.getIsbn());
				book.setStatus(bookEntity.getStatus());
				book.setRating(bookEntity.getRating());
				book.setTitle(bookEntity.getTitle());
				book.setBookId(bookEntity.getBookId());
				bookList.add(book);
			}
			customer.setBookList(bookList);
			
			return customer;
		}
	}

}
