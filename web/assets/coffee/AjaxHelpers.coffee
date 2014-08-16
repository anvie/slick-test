
window.dg = window.dg || {}
window.dg.Ajax = {}

class dg.Ajax
    @confirm: (title, ajaxId) ->
        if confirm(title)
            liftAjax.lift_ajaxHandler(ajaxId + "=true", null, null, null)
        return

    @inWaiting = {}

    @buttonWait: (me, token) ->
        if @inWaiting[token]
            return

        id = dg.Util.uuid()

        iconOnly = $(me).hasClass("icon") and $.trim($(me).text())==""
        isReplace = $(me).hasClass("loading-replace")

        # merupakan style loading menggunakan progress bar yang melayang
        # dibawah navbar bagian header
        isHeadNavProgress = $(me).hasClass("head-nav-progress")
        prog = null

        loadingClass = "icon icon-refresh icon-spin"
        removedClass = ""

        _done = ()->
            self = @
            setTimeout ()->
                self.inWaiting[token] = false
                $(me).removeAttr("disabled")

                if isHeadNavProgress
                    prog.end()
                else
                    if removedClass.length > 0
                        $(me).removeClass(loadingClass)
                        $(me).addClass("class", removedClass)


                    if isReplace
                        $("#" + id).remove()
                        $(me).show()
                    else
                        if not iconOnly
                            $("#" + id).remove()

                return
            , 300
            return

        @inWaiting[token] = true

        buildLoadingAnim = ->
            $('<span id="' + id + '" class="' + loadingClass + '"> </span> ')

        if isReplace
            $(me).hide()
            buildLoadingAnim().insertAfter($(me))

        else if isHeadNavProgress

            $(me).attr("disabled", "disabled")

            prog = progressJs(".navbar-fixed-top")
            prog.setOptions({overlayMode: true, theme: 'green', positionStyle: 'fixed'})
            prog.start()
            prog.increase(10)
            setTimeout ()->
                prog.increase(10)
                return
            , 300

        else
            # periksa apakah hanya icon tanpa text?
            if iconOnly
                ss = $(me).attr("class").split(" ")

                ss = $.grep(ss, (n,i) => n.indexOf("icon-") > 1 or n == "icon")

                removedClass = ss.join(" ")

                $(me).attr("disabled", "disabled").removeClass(removedClass)
                .addClass(loadingClass)
            else
                $(me).attr("disabled", "disabled")
                .prepend(buildLoadingAnim())

        liftAjax.lift_ajaxHandler(token + "=true", ()=>
            _done.call(@)
            return
        , ()=>
            _done.call(@)
            return
        , null)

        false



    @dispatch: (funcId, success, error) ->
        liftAjax.lift_ajaxHandler(funcId + "=true", success, error, null)
        return

    @post: (url, data={}, success, error) ->

        urlData = Object.keys(data).map((k)->
            encodeURIComponent(k) + '=' + encodeURIComponent(data[k])
        ).join('&')

        $.ajax {
            'url' : url,
            'type': 'POST',
            'contentType': "application/json",
            'data': urlData,
            'success': success,
            'error': error
        }

    @put: (url, data={}, success, error) ->

        urlData = Object.keys(data).map((k)->
            encodeURIComponent(k) + '=' + encodeURIComponent(data[k])
        ).join('&')

        $.ajax {
            'url' : url,
            'type': 'PUT',
            'contentType': "application/json",
            'data': urlData,
            'success': success,
            'error': error
        }

    @get: (url, data={}, success, error) ->

        urlData = Object.keys(data).map((k)->
            encodeURIComponent(k) + '=' + encodeURIComponent(data[k])
        ).join('&')

        $.ajax {
            'url' : url,
            'type': 'GET',
            'contentType': "application/json",
            'data': urlData,
            'success': success,
            'error': error
        }

    @delete: (url, success, error, complete) ->
        $.ajax {
            'url' : url,
            'type': 'DELETE',
            'contentType': "application/json",
            'success': success,
            'error': error,
            'complete': complete
        }

    @shareToFb: (url, text) ->

        D=550
        A=450
        C=screen.height
        B=screen.width
        H=Math.round((B/2)-(D/2))
        G=0
        if C > A
            G = Math.round((C/2)-(A/2))

        #        enc = encodeURIComponent
        #        text = enc(text)
        #        url = enc(url)

        sharerUrl = "http://www.facebook.com/sharer.php?src=bm&v=4&i=1304066057&u=#{url}&t=#{text}"

        _window_opener = () ->
            window.open sharerUrl, 'sharer', 'left='+H+',top='+G+',toolbar=0,status=0,resizable=1,width='+D+',height='+A

        if /Firefox/.test(navigator.userAgent)
            setTimeout _window_opener, 0
        else
            _window_opener()


    @shareToTw: (url, text) ->

        D=550
        A=450
        C=screen.height
        B=screen.width
        H=Math.round((B/2)-(D/2))
        G=0
        if C > A
            G = Math.round((C/2)-(A/2))

        #        enc = encodeURIComponent
        #        text = enc(text)
        #        url = enc(url)

        sharerUrl = "http://twitter.com/share?url=#{url}&original_referer=#{url},related=mindtalk:Official Mindtalk&text=#{text}+%23Mindtalk"

        window.open sharerUrl, 'sharer', 'left='+H+',top='+G+',toolbar=0,status=0,resizable=1,width='+D+',height='+A


    @showMore: (me, token) ->
        if @inWaiting[token]
            return

        loadingClass = "icon icon-refresh icon-spin"
        removedClass = ""

        _done = ()->
            self = @
            setTimeout ()->
                self.inWaiting[token] = false
                $(me).removeAttr("disabled")

                if removedClass.length > 0
                    $(me).removeClass(loadingClass)
                    $(me).addClass("class", removedClass)

                return
            , 1000
            return

        @inWaiting[token] = true

        # periksa apakah hanya icon tanpa text?
        if $(me).hasClass("icon") and $.trim($(me).text())==""
            ss = $(me).attr("class").split(" ")

            ss = $.grep(ss, (n,i) => n.indexOf("icon-") > 1 or n == "icon")

            removedClass = ss.join(" ")

            $(me).attr("disabled", "disabled").removeClass(removedClass)
            .addClass(loadingClass)
        else
            $(me).attr("disabled", "disabled").prepend('<span class="' + loadingClass + '"> </span> ')

        maxId = $(me).data("max-id")
        offset = null
        limit = null

        if not maxId
            offset = $(me).data("offset")
            limit = $(me).data("limit")

        param = ""
        if maxId
            param = token + "=true&maxId=" + maxId
        else if offset? and limit?
            param = token + "=true&offset=" + offset + "&limit=" + limit
        else
            throw "No maxId, offset, nor limit specified"

        liftAjax.lift_ajaxHandler(param, ()=>
            _done.call(@)
            return
        , ()=>
            _done.call(@)
            return
        , null)

        false

    ###
    Helper untuk input text yang kalo ditekan enter akan
    langsung melakukan ajax dispatch.
    ###
    @textReturnPress: (event, me, funcId) ->
#        log.print(event)

        if event.keyCode != 13
            return

        text = $.trim($(me).val())

        if text.length == 0
            return

        $(me).attr("disabled","disabled")

        coord = $(me).offset()
        left = ((coord.left + $(me).width()) - 6) + "px"
        top = (coord.top + 6) + "px"
        $spinner = dg.Util.showLoadingAnimAt($(me), "free", left, top, null, null)

        success = (data)->
            $(me).val("").removeAttr("disabled").focus()
            $spinner.remove()
            return

        liftAjax.lift_ajaxHandler("#{funcId}=" + encodeURIComponent(text), success)

        return
