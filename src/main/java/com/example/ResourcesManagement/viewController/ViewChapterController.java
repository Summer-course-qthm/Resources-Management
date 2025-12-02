package com.example.ResourcesManagement.viewController;


import com.example.ResourcesManagement.DTO.request.ChapterRequestDTO;
import com.example.ResourcesManagement.DTO.response.ChapterResponseDTO;
import com.example.ResourcesManagement.service.ChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;


@Controller
public class ViewChapterController {

    @Autowired
    ChapterService chapterService;
    // lấy danh sách chapter
    @GetMapping("/viewDepartments")
    public String viewChapters(Model model) {
        List<ChapterResponseDTO> chapters = chapterService.getChapters();

        model.addAttribute("chapters" , chapters);
        return "chapter";
    }

    // thêm chapter
    @GetMapping("/chapter/add")
    public String showAddChapterForm(Model model) {
        model.addAttribute("chapterRequest", new ChapterRequestDTO());
        return "createChapter"; // Trả về file HTML createChapter
    }
    @PostMapping("/chapter/add")
    public String saveChapter(@ModelAttribute("chapterRequest") ChapterRequestDTO chapterRequest) {
        chapterService.addChapter(chapterRequest);
        return "redirect:/viewDepartments";
    }

    //sửa chapter
    @GetMapping("/chapters/edit/{id}")
    public String showEditChapterForm(@PathVariable Long id, Model model) {
        ChapterRequestDTO chapterRequest = chapterService.getChapterById(id);
        model.addAttribute("chapterRequest", chapterRequest);
        model.addAttribute("chapterId", id); // Đánh dấu là đang sửa ID này
        return "createChapter";
    }
    // 5. Xử lý Cập nhật
    @PostMapping("/chapters/edit/{id}")
    public String updateChapter(@PathVariable Long id, @ModelAttribute("chapterRequest") ChapterRequestDTO chapterRequest) {
        // Lưu ý: Hàm updateChapter trong Service của bạn đang nhận ChapterResponseDTO
        // Nếu bạn muốn chuẩn hơn thì nên sửa Service nhận ChapterRequestDTO
        chapterService.updateChapter(id, chapterRequest);
        return "redirect:/viewDepartments";
    }

    // xóa chapter
    @GetMapping("/chapter/delete/{id}")
    public String deleteChapter(@PathVariable Long id) {
        chapterService.deleteChapter(id);
        return "redirect:/viewDepartments";
    }
}
