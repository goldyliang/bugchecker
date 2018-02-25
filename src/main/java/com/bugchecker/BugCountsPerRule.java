package com.bugchecker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by elnggng on 2/25/18.
 */
public class BugCountsPerRule {
    private Map< String, long[] > countBugs = new HashMap<String, long[]>();

    public void addCount (Bug bug) {
        String ruleName = bug.getRule().getName();
        long[] cnt = countBugs.get(ruleName);
        if (cnt != null) {
            cnt[0] = cnt[0] + 1;
        } else {
            cnt = new long[1];
            cnt[0] = 1;
            countBugs.put(ruleName, cnt);
        }
    }

    public void addCount (BugCountsPerRule countsToAdd) {
        for (Map.Entry<String, long[]> entry : countsToAdd.countBugs.entrySet()) {
            String ruleName = entry.getKey();
            long cnt = entry.getValue()[0];
            long[] oldCnt = countBugs.get(ruleName);
            if (oldCnt != null) {
                oldCnt[0] = oldCnt[0] + entry.getValue()[0];
            } else {
                oldCnt = new long[1];
                oldCnt[0] =cnt;
                countBugs.put(ruleName, oldCnt);
            }
        }
    }

    public List<String> getListRuleNames() {
        List<String> list = new ArrayList<String>();
        list.addAll(countBugs.keySet());
        return list;
    }

    public List<Long> getListCountValues(List<String> ruleList) {
        List<Long> list = new ArrayList<Long> ();
        ruleList.forEach( rule -> {
                    long[] value = countBugs.get(rule);
                    if (value != null) {
                        list.add(countBugs.get(rule)[0]);
                    } else {
                        list.add(Long.valueOf(0));
                    }
                }
        );
        return list;
    }

    public String getStringCountValues(List<String> ruleList, String delimilator) {
        return getListCountValues(ruleList).stream()
                .map(Object::toString)
                .collect(Collectors.joining(delimilator));
    }
}
