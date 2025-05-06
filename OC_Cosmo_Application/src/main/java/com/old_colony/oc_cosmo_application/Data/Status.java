package com.old_colony.oc_cosmo_application.Data;

/**
 * User status given to each user,
 * allows some pages to become visible
 * and usable if status is high enough
 * <table border="1">
 *     <tr>
 *         <th>SQL int status</th>
 *         <th>Status</th>
 *     </tr>
 *     <tr>
 *         <td>0</td>
 *         <td>STUDENT</td>
 *     </tr>
 *     <tr>
 *         <td>1</td>
 *         <td>ADMIN</td>
 *     </tr>
 *     <tr>
 *         <td>Other</td>
 *         <td>ERROR</td>
 *     </tr>
 * </table>
 * Add more values if this project expands in the future
 */

public enum Status {
    ERROR,
    STUDENT,
    ADMIN;

    public boolean isAdmin() {
        return this == ADMIN;
    }
}