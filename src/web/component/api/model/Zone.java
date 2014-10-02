/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.api.model;

import java.util.List;

/**
 *
 * @author Hiroshi
 */
public interface Zone extends Comparable<Zone>{
    public List<String> getMessages();
    public String getName();
    public String getRegionName();
    public String getState();
}
