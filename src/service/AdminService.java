// === THEME PARK + RESORT SYSTEM - CORRECT VERSION ===
package service;
import model.*; import repository.*; import java.util.*; import java.util.Optional;
public class AdminService {
    private final UserRepository userRepo; private final ReportRepository reportRepo;
    public AdminService(UserRepository userRepo, ReportRepository reportRepo) { this.userRepo=userRepo; this.reportRepo=reportRepo; }
    public Admin registerAdmin(String userID, String email) {
        if(userRepo.findByEmail(email).isPresent()){System.out.println("[ADMIN SERVICE] ❌ Email in use: "+email);return null;}
        Admin a=new Admin(userID,email); a.register(); a.issueSessionToken(); userRepo.save(a);
        System.out.println("[ADMIN SERVICE] ✅ Admin saved to DB.\n"); return a;
    }
    public Report generateReport(Admin admin, String type) {
        String id="RPT-"+type.toUpperCase()+"-"+(System.currentTimeMillis()%100000);
        Report r=admin.generateReport(id,type.toUpperCase()); reportRepo.save(r);
        System.out.println("[ADMIN SERVICE] ✅ Report saved to DB."); r.exportPDF(); r.exportExcel(); System.out.println(); return r;
    }
    public List<Report> getAllReports(){ return reportRepo.findAll(); }
    public List<User>   getAllUsers()  { return userRepo.findAll(); }
}
