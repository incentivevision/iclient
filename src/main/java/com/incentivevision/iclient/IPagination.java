package com.incentivevision.iclient;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ranatayyab on 10/29/17.
 */

public class IPagination {
    private String firstLink;
    private String lastLink;
    private String prevLink;
    private String nextLink;

    private String currentPage;
    private String lastPage;
    private String from;
    private String to;
    private String perPage;
    private String path;
    private String total;

    /**
     * @param links
     * @param meta
     * @throws JSONException
     */
    public IPagination(JSONObject links, JSONObject meta) throws JSONException {

        firstLink = links.getString("first");
        lastLink = links.getString("last");
        prevLink = links.getString("prev");
        nextLink = links.getString("next");

        currentPage = meta.getString("current_page");
        lastPage = meta.getString("last_page");
        from = meta.getString("from");
        to = meta.getString("to");
        perPage = meta.getString("per_page");
        path = meta.getString("path");
        total = meta.getString("total");

    }


    /**
     * @return
     */
    public Boolean canLoadMore() {
        return Integer.parseInt(total) > Integer.parseInt(to);
    }

    /**
     * @return
     */
    public String getNextPage() {
        return String.valueOf(Integer.parseInt(currentPage) + 1);
    }

    /**
     * @return
     */
    public String getLastPage() {
        return lastPage;
    }

    /**
     * @return
     */
    public String getFirstLink() {
        return firstLink;
    }

    /**
     * @return
     */
    public String getLastLink() {
        return lastLink;
    }

    /**
     * @return
     */
    public String getPrevLink() {
        return prevLink;
    }

    /**
     * @return
     */
    public String getNextLink() {
        return nextLink;
    }

    /**
     * @return
     */
    public String getCurrentPage() {
        return currentPage;
    }

    /**
     * @return
     */
    public String getFrom() {
        return from;
    }

    /**
     * @return
     */
    public String getTo() {
        return to;
    }

    /**
     * @return
     */
    public String getPerPage() {
        return perPage;
    }

    /**
     * @return
     */
    public String getPath() {
        return path;
    }

    /**
     * @return
     */
    public String getTotal() {
        return total;
    }
}
