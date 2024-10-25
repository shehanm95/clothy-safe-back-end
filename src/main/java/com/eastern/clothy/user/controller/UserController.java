package com.eastern.clothy.user.controller;


import com.eastern.clothy.user.model.AppUser;
import com.eastern.clothy.user.model.UserRole;
import com.eastern.clothy.user.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserRepo userRepo;

    @PostMapping("/authUser")
    public ResponseEntity<AppUser> authUser(@RequestBody AppUser user) {
        AppUser dbUser = userRepo.getUserByUsername(user.getUsername());
        if(user.getUsername().equals("admin") && user.getPassword().equals("Temp@123") && userRepo.count() ==1){
            AppUser firstUser = new AppUser(
                    userRepo.count(),
                    "admin@clothy.com",
                    "admin",
                    "null",
                    "Clothy Admin",
                    UserRole.ADMIN,
                    (int)userRepo.count()
            );
            return ResponseEntity.ok(firstUser);
        }
        System.out.println(dbUser);


        try {
            if (dbUser != null &&  hashPassword(user.getUsername()).equals(dbUser.getPassword())){
               dbUser.setPassword(null);
               return ResponseEntity.ok(dbUser);
            }
            else {
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(null);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return null;
    }


    @PostMapping("/saveUser")
    public ResponseEntity<AppUser> saveUser(@RequestBody AppUser user) throws NoSuchAlgorithmException {
        System.out.println(user);
        user.setPassword(hashPassword(user.getPassword()));
        AppUser savedUser = userRepo.save(user);
        System.out.println(savedUser);
        return ResponseEntity.ok(savedUser);

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable long id) {
        if (userRepo.existsById(id)) {
            userRepo.deleteById(id);
            System.out.println("User deleted with ID: " + id);
            return ResponseEntity.ok("User deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

        private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(password.getBytes());
        return HexFormat.of().formatHex(hashBytes);  // Convert bytes to hex string
    }

    @GetMapping("/all")
    public List<AppUser> getAllUsers (){
        System.out.println("users returned");
      return  userRepo.findAll();
    }


    @GetMapping("/get/{id}")
    public ResponseEntity<AppUser> getUserById(@PathVariable Long id) {
        // Find the user by id, throw 404 if not found
        return userRepo.findById(id)
                .map(user -> ResponseEntity.ok().body(user))
                .orElse(ResponseEntity.notFound().build());
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<AppUser> updateUser(@PathVariable Long id, @RequestBody AppUser updatedUser) {
        // Check if the user with the given id exists
        return userRepo.findById(id)
                .map(existingUser -> {
                    // Update the existing user's details with the new data
                    existingUser.setUsername(updatedUser.getUsername());
                    existingUser.setEmail(updatedUser.getEmail());
                    existingUser.setFullName(updatedUser.getFullName());
                    existingUser.setRole(updatedUser.getRole());
                    existingUser.setCartId(updatedUser.getCartId());

                    // Save the updated user back to the repository
                    AppUser savedUser = userRepo.save(existingUser);

                    // Return the updated user
                    return ResponseEntity.ok().body(savedUser);
                }).orElse(ResponseEntity.notFound().build()); // Return 404 if user not found
    }

}