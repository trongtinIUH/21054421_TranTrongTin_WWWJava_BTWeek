package iuh.fit.baitap_week5.fontend.controllers;

import com.neovisionaries.i18n.CountryCode;
import iuh.fit.baitap_week5.backend.models.Address;
import iuh.fit.baitap_week5.backend.models.Candidate;
import iuh.fit.baitap_week5.backend.models.Job;
import iuh.fit.baitap_week5.backend.models.Skill;
import iuh.fit.baitap_week5.backend.reponsitories.AddressRepository;
import iuh.fit.baitap_week5.backend.reponsitories.CandidateRepository;
import iuh.fit.baitap_week5.backend.services.AddressServices;
import iuh.fit.baitap_week5.backend.services.CandidateServices;
import iuh.fit.baitap_week5.backend.services.JobServices;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class CandidateController {
    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private CandidateServices candidateServices;

    @Autowired
    private AddressServices addressServices;

    @Autowired
    private JobServices jonServices;
    @Autowired
    private JobServices jobServices;

    //hiển thị form xóa ứng viên
    @GetMapping("/candidate/delete-candidate")
    public String showDeleteCandidateForm() {
        return "candidates/delete-candidate";
    }

    @Transactional
    @PostMapping("/candidate/delete-candidate")
    public String deleteCandidate(@RequestParam("email") String email, Model model) {
        Candidate candidate = candidateRepository.findByEmail(email);
        if (candidate == null) {
            model.addAttribute("error", "Không tìm thấy ứng viên với email: " + email);
            return "candidates/delete-candidate";
        }

        candidateRepository.delete(candidate);
        model.addAttribute("message", "Candidate deleted successfully!");
        return "candidates/delete-candidate";
    }


    // Hiển thị form thêm Candidate
    @GetMapping("/candidate/add-candidate")
    public String showAddCandidateForm() {
        return "candidates/add-candidate";
    }

    @PostMapping("/candidate/add-candidate")
    public String addCandidate(
            @RequestParam("fullName") String fullName,
            @RequestParam("dob") String dob,
            @RequestParam("street") String street,
            @RequestParam("city") String city,
            @RequestParam("country") String country,
            @RequestParam("number") String number,
            @RequestParam("zipcode") String zipcode,
            @RequestParam("phone") String phone,
            @RequestParam("email") String email,
            Model model) {

        // Kiểm tra email đã tồn tại
        if (candidateRepository.findByEmail(email) != null) {
            model.addAttribute("error", "Email đã tồn tại trong hệ thống.");
            return "candidates/add-candidate";
        }

        try {
            CountryCode countryCode = CountryCode.valueOf(country.toUpperCase());
            Address address = new Address(street, city, countryCode, number, zipcode);

            addressServices.saveAddress(address);

            Candidate candidate = new Candidate(fullName, LocalDate.parse(dob), address, phone, email);
            candidateRepository.save(candidate);

            model.addAttribute("message", "Candidate added successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Error adding candidate: " + e.getMessage());
        }

        return "candidates/add-candidate";
    }


    @GetMapping("/listkpt")
    public String showCandidateList(Model model) {
        model.addAttribute("candidates", candidateRepository.findAll());
        return "candidates/candidates";
    }


    //findall có phân trang
    @GetMapping("/list")
    public String showCandidateListPaging(Model model,
                                          @RequestParam("page") Optional<Integer> page,
                                          @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);
        Page<Candidate> candidatePage = candidateServices.findAll(currentPage - 1, pageSize, "id", "asc");

        model.addAttribute("candidatePage", candidatePage);
        int totalPages = candidatePage.getTotalPages();

        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "candidates/candidates-paging";
    }



    @GetMapping("/candidate/login")
    public String showLoginForm() {
        return "login"; // tên template của trang đăng nhập
    }

    @PostMapping("/candidate/login")
    public String loginCandidate(@RequestParam("email") String email, Model model) {
        Candidate candidate = candidateServices.findByEmail(email);
        if (candidate == null) {
            model.addAttribute("error", "Email không tồn tại. Vui lòng thử lại.");
            return "login";
        }

        // Lấy danh sách công việc gợi ý dựa trên kỹ năng của ứng viên
        List<Job> suggestedJobs = jobServices.findSuggestedJobsForCandidate(candidate);
        model.addAttribute("candidate", candidate);
        model.addAttribute("suggestedJobs", suggestedJobs);

        return "suggested-jobs"; // Trang hiển thị danh sách công việc
    }


    //câu 6 tìm ứng viên có skill phù hợp
    @GetMapping("/candidate/skill_search")
    public String showSkillSearchForm() {
        return "skill-search";
    }

    @PostMapping("/candidate/skill_search")
    public String searchCandidateBySkill(@RequestParam("skillName") String skillName, Model model) {
        List<Candidate> candidates = candidateServices.findCandidatesBySkill(skillName);
        model.addAttribute("UngVienPhuHop", candidates);
        model.addAttribute("skillName", skillName);
        return "sugggested-skill-search";
    }


    //câu 7
    @GetMapping("/candidate/login_learnSkill")
    public String showLoginForm_Learn() {
        return "login_learn"; // tên template của trang đăng nhập
    }

    @PostMapping("/candidate/login_learnSkill")
    public String loginCandidate_learn(@RequestParam("email") String email, Model model) {
        Candidate candidate = candidateServices.findByEmail(email);
        if (candidate == null) {
            model.addAttribute("error", "Email không tồn tại. Vui lòng thử lại.");
            return "login_learn"; // Quay lại trang đăng nhập nếu email không tồn tại
        }

        List<Skill> suggestedSkills = candidateServices.getSuggestedSkillsForCandidate(email);
        model.addAttribute("suggestedSkills", suggestedSkills);
        model.addAttribute("candidate", candidate);
        return "suggested-learn-skill";
    }


}
