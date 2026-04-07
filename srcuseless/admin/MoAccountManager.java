package admin;

import common.Constants;
import common.CsvUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MoAccountManager {
    private static final Pattern STAFF_ID_PATTERN = Pattern.compile(Constants.STAFF_ID_REGEX);

    public boolean addMoAccount(MoAccount moAccount) {
        if (!STAFF_ID_PATTERN.matcher(moAccount.getStaffId()).matches()) {
            System.out.println("新增失败：工号格式不合法");
            return false;
        }

        List<MoAccount> allMoList = getAllMoAccounts();
        for (MoAccount mo : allMoList) {
            if (mo.getStaffId().equals(moAccount.getStaffId())) {
                System.out.println("新增失败：工号已存在");
                return false;
            }
        }

        List<String> writeList = new ArrayList<>();
        writeList.add(moAccount.toString());
        CsvUtil.writeCsv(Constants.MO_ACCOUNT_FILE, writeList, true);
        System.out.println("新增成功：" + moAccount.getMoName());
        return true;
    }

    public boolean deleteMoAccount(String staffId) {
        List<MoAccount> allMoList = getAllMoAccounts();
        boolean exists = false;
        List<String> newMoList = new ArrayList<>();
        newMoList.add("staffId,moName,email,status");

        for (MoAccount mo : allMoList) {
            if (mo.getStaffId().equals(staffId)) {
                exists = true;
                System.out.println("即将删除：" + mo.getMoName());
                continue;
            }
            newMoList.add(mo.toString());
        }

        if (!exists) {
            System.out.println("删除失败：工号不存在");
            return false;
        }

        CsvUtil.writeCsv(Constants.MO_ACCOUNT_FILE, newMoList, false);
        System.out.println("删除成功：" + staffId);
        return true;
    }

    public boolean updateMoStatus(String staffId, int status) {
        List<MoAccount> allMoList = getAllMoAccounts();
        boolean exists = false;
        List<String> newMoList = new ArrayList<>();
        newMoList.add("staffId,moName,email,status");

        for (MoAccount mo : allMoList) {
            if (mo.getStaffId().equals(staffId)) {
                exists = true;
                mo.setStatus(status);
                System.out.println("状态修改：" + mo.getMoName() + " → " + (status == 1 ? "启用" : "禁用"));
            }
            newMoList.add(mo.toString());
        }

        if (!exists) {
            System.out.println("状态修改失败：工号不存在");
            return false;
        }

        CsvUtil.writeCsv(Constants.MO_ACCOUNT_FILE, newMoList, false);
        return true;
    }

    public List<MoAccount> getAllMoAccounts() {
        List<MoAccount> moList = new ArrayList<>();
        List<String> csvList = CsvUtil.readCsv(Constants.MO_ACCOUNT_FILE);

        for (int i = 0; i < csvList.size(); i++) {
            String line = csvList.get(i);
            if (i == 0 && line.contains("staffId")) continue;

            String[] fields = line.split(",");
            if (fields.length == 4) {
                MoAccount mo = new MoAccount();
                mo.setStaffId(fields[0]);
                mo.setMoName(fields[1]);
                mo.setEmail(fields[2]);
                mo.setStatus(Integer.parseInt(fields[3]));
                moList.add(mo);
            }
        }
        return moList;
    }

    public static void main(String[] args) {
        MoAccountManager manager = new MoAccountManager();

        MoAccount mo1 = new MoAccount("BUPT123456", "LiLei", "lilei@qm.com", Constants.MO_STATUS_ENABLE);
        manager.addMoAccount(mo1);

        MoAccount mo2 = new MoAccount("BUPT123456", "HanMeimei", "hanmeimei@qm.com", Constants.MO_STATUS_ENABLE);
        manager.addMoAccount(mo2);

        MoAccount mo3 = new MoAccount("123456", "WangWu", "wangwu@qm.com", Constants.MO_STATUS_ENABLE);
        manager.addMoAccount(mo3);

        System.out.println("\n当前所有MO账号：");
        for (MoAccount mo : manager.getAllMoAccounts()) {
            System.out.println(mo.getStaffId() + " | " + mo.getMoName() + " | " + (mo.getStatus() == 1 ? "启用" : "禁用"));
        }

        manager.updateMoStatus("BUPT123456", Constants.MO_STATUS_DISABLE);
        manager.deleteMoAccount("BUPT123456");

        System.out.println("\n删除后：");
        if (manager.getAllMoAccounts().isEmpty()) {
            System.out.println("无MO账号");
        }
    }
}