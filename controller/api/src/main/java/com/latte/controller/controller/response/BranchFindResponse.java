package com.latte.controller.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BranchFindResponse {
    private List<String> branches;

    public static BranchFindResponse from(List<String> branches) {
        return new BranchFindResponse(branches);
    }
}
