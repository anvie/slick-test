window.dg = window.dg || {}
window.dg.Util = {}

class dg.Util

    @showError: (text) ->
        bootbox.alert('<div class="alert alert-danger"><span class="icon-warning-sign"></span> ' + text + '</div>');
        return

    @notice: (text) ->
        bootbox.alert({message: text, className: "bootbox-alert", buttons : { ok : {className : "btn-info"}}})
        return


    @bootstrapCalled = false

    @bootstrap: () ->
        if @bootstrapCalled
            return

        @bootstrapCalled = true

#        d = document

#        @setupHotKeys()

        return



    @bind: (eventName, func = ()->) ->
        $(document).bind(eventName, func)
        return

    @rebindCalled = false

    @rebind: (currentUrl=window.location.href)->

        if @rebindCalled
            return

        @rebindCalled = true

        count = 0
        delay = 300

        # @TODO(*): perlu optimasi ini, mungkin bisa menggunakan jQuery.on
        rebindInternal = ()->

            # jangan proses apabila ada di mode mobile
            if dg.Conf.is_mobile
                return

            # semua element yang punya attribut data-embed-url akan otomatis dispawn url-nya pake popup modal view

            $p = $("a[data-embed-url]:not([data-embed-url-binded])")
            if $p.length > 0
                $p.unbind("click")
                $p.bind "click", (e) ->
                    try

                        $.magnificPopup.close()

                        url = $(this).attr("href")
                        embedUrl = $(this).attr("data-embed-url")
                        dataClass = $(this).attr("data-mfp-class")
                        title = $(this).attr("title")

                        title = if title then title else "Mindtalk"

                        if not embedUrl or embedUrl.length < 1
                            return

                        e.preventDefault()
#                        History.pushState(null, null, url)

                        data = {
                            loc: 'show-modal',
                            url: url,
                            embedUrl: embedUrl,
                            dataClass: dataClass,
                            currentUrl: currentUrl,
                        }

                        dg.History.push(url, title, data)

                    catch e
                        log.print(e)
                    # pass

                $p.attr("data-embed-url-binded","true");

                count = 0
                delay = 300

            count = count + 1
            delay = delay + (count * 7)
#            console.log("new delay: " + delay)

            if count > 30
                count = 0
                delay = 300

            clearTimeout(_ival)
            _ival = setTimeout rebindInternal, delay

            if $("div.post-content").length > 0
                dg.Util.compileText()

        _ival = setTimeout rebindInternal, delay

        return


    @absUrl: (relativeUrl) ->
        if relativeUrl.indexOf("http") != 0
            if relativeUrl.indexOf("/") == 0
                dg.Conf.baseUrl + relativeUrl
            else
                dg.Conf.baseUrl + "/" + relativeUrl
        else
            relativeUrl

    @modalIframe: (url, closeHistoryUrl="", afterCloseAction=()->) ->
        if not $.magnificPopup?
            log.error("no $.magnificPopup")
            return

        $.magnificPopup.open {
            "iframe": {
                "markup": '<div class="mfp-iframe-scaler">'+
                '<div class="mfp-close"></div>'+
                '<iframe class="mfp-iframe" frameborder="0" allowfullscreen style="overflow: hidden;"></iframe>'+
                '</div>'
            },
            "items": {
                "src": url
            },
            "type": "iframe",
            "callbacks": {
                "close": () ->
                    if closeHistoryUrl.length > 0
#                        History.replaceState(null, null, closeHistoryUrl)
                        dg.History.push(closeHistoryUrl)

                    afterCloseAction()

                    return
            }
        }
        return

    @modalDialog: (html) ->
        $.magnificPopup.open {
            "items": {"src": html},
            "type": "inline"
        }
        return


    @hideAllModal: ()->
        try
            bootbox.hideAll()
        catch e
            # pass
        try
            $.magnificPopup.close()
        catch e
            # pass

        return

    @dispatchEventParent: (eventName, data={}) ->
        parent.$('body').trigger(eventName, data);
        return

    @setupHotKeys: () ->

        # cheat untuk melihat current running version
        # dan server di app ini jalan
        Mousetrap.bind "v v v", ()->
            bootbox.alert('<div>version: ' + dg.Conf.core_version + '</div>' +
                '<div>git: ' + dg.Conf.gitHash + '</div>' +
                '<div>machine: ' + dg.Conf.machine + '</div>')
            return

        # cheat untuk menjalankan autopilot
        # digunakan biasanya untuk live performance testing
        Mousetrap.bind "a a p l", ()->
            if sessionStorage?
                if sessionStorage["autoPilot"] == "1"
                    log.info("autopilot deactivated")
                    sessionStorage["autoPilot"] = "0"
                else
                    log.info("autopilot activated")
                    sessionStorage["autoPilot"] = "1"
            dg.Util.runAutopilot()
        return

    @normalizeTimeSeriesData: (data) ->
      nowt = new Date()
      newData = []
      if data.length is 0
        i = 0

        while i < 8
          d2 = new Date(nowt.getUTCFullYear(), nowt.getMonth(), nowt.getDate() - i)
          kt = d2.getTime() # + 25000000;
          newData.push [kt, 0]
          i++

      else

        newData = [data[0]]
        i = 1
        while i < data.length
          diff = @dateDiff(data[i - 1][0], data[i][0])
          startDate = new Date(data[i - 1][0])
          if diff > 1
            j = 0
            while j < diff - 1
              fillDate = new Date(startDate).setDate(startDate.getDate() + (j + 1))
              newData.push [fillDate, 0]
              j++
          newData.push data[i]
          i++
      newData

    # helper function to find date differences
    @dateDiff = (d1, d2) ->
      Math.floor (d2 - d1) / (1000 * 60 * 60 * 24)


    @S4 = -> ((1 + Math.random()) * 0x10000 | 0).toString(16).substring 1
    @uuid = ()->
        "" + (@S4()) + (@S4()) + "-" + (@S4()) + "-" + (@S4()) + "-" + (@S4()) + "-" + (@S4()) + (@S4()) + (@S4())


    ###
    Untuk memeriksa apakah suatu element terlihat di viewport/screen
    user?
    Ini bermanfaat untuk misalnya mendeteksi ketika tombol show more
    terlihat dan melakukan aksi click pada tombol tersebut.
    ###
    @isElementInViewport = (el) ->

        h = 0.33

        getOffset = (el) ->
            offsetTop = 0
            offsetLeft = 0
            loop
                offsetTop += el.offsetTop  unless isNaN(el.offsetTop)
                offsetLeft += el.offsetLeft  unless isNaN(el.offsetLeft)
                break unless el = el.offsetParent
            top: offsetTop
            left: offsetLeft

        scrolled = window.pageYOffset

        docElem = window.document.documentElement;

        client = docElem["clientHeight"]
        inner = window["innerHeight"]

        viewportH = if (client < inner) then inner else client


        viewed = scrolled + viewportH
        elH = el.offsetHeight
        elTop = getOffset(el).top
        elBottom = elTop + elH
        h = h or 0
        (elTop + elH * h) <= viewed and (elBottom) >= scrolled

    ###
    Helper untuk input text yang kalo ditekan enter akan
    langsung melakukan ajax dispatch.
    ###
    @ajaxTextReturnPress: (event, me, funcId) ->
        log.print(event)

        if event.keyCode != 13
            return

        text = $.trim($(me).val())

        if text.length == 0
            return

        $(me).attr("disabled","disabled")
        success = (data)->
            $(me).val("").removeAttr("disable").focus()

        liftAjax.lift_ajaxHandler("#{funcId}=" + encodeURIComponent(text), success)

        return

    @showLoadingAnimAt: ($elmAt, placementMethod="after", left, top, right, bottom) ->
        $spinner = $('<span class="icon icon-refresh icon-spin" style="position: absolute;"></span>')

        if left
            $spinner.css("left", left)

        if top
            $spinner.css("top", top)

        if right
            $spinner.css("right", right)

        if bottom
            $spinner.css("bottom", bottom)

        switch placementMethod
            when "after"
                $spinner.insertAfter($elmAt)
            when "replace"
                $elmAt.replaceWith($spinner)
            when "append"
                $elmAt.append($spinner)
            when "free"
                $("body").append($spinner)

        $spinner

    @unescapeHtml: (text) ->
        e = document.createElement("div")
        e.innerHTML = text
        (if e.childNodes.length is 0 then "" else e.childNodes[0].nodeValue)

    ###
        digunakan untuk mengambil value dari parameter url
        ex : dg.Util.urlParam("rid")
    ###
    @urlParam: (name) ->
        results = new RegExp('[\\?&amp;]' + name + '=([^&amp;#]*)').exec(window.location.href);
        if (results==null)
            return null
        else
            return results[1] || 0

    ## Untuk mendapatkan cookie value
    # berdasarkan key-nya.
    @getCookie: (name) ->
        value = "; " + document.cookie
        parts = value.split("; " + name + "=")
        parts.pop().split(";").shift() if parts.length is 2



# @TODO(robin): tempatkan ini pada tempatnya
assert = (exp, msg)->
    if not exp
        throw msg
    return

$(document).ready ()->
    dg.Util.bootstrap()
    return
