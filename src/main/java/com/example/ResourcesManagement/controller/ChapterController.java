package com.example.ResourcesManagement.controller;

import com.example.ResourcesManagement.DTO.response.ChapterResponseDTO;
import com.example.ResourcesManagement.service.ChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ChapterController {

    @Autowired
    ChapterService chapterService;

    @GetMapping("/chapters")
    public ResponseEntity<List<ChapterResponseDTO>> getChapters() {
        List<ChapterResponseDTO> chapters = chapterService.getChapters();
        return ResponseEntity.ok().body(chapters);

    }

    //update chapter
    @PutMapping("/chapter/update/{id}")
    public ResponseEntity<String> updateChapter(@PathVariable Long id, @RequestBody ChapterResponseDTO chapterResponseDTO) {
        chapterService.updateChapter(id, chapterResponseDTO);
        return ResponseEntity.ok("Update successfully");
    }

    //xóa user khỏi chapter( dùng PUT bên user cập nhập chapterId = null)
    // chuyển thành viên qua chapter khác
        // hai chức năng này có dùng thng qua  @PutMapping("/user/removeFromChapter/{id}")


    // xóa chapter
    @DeleteMapping("/chapter/delete/{id}")
    public ResponseEntity<String> deleteChapter(@PathVariable Long id) {
        chapterService.deleteChapter(id);
        return ResponseEntity.ok("Delete successfully");
    }



}
